package name.martingeisse.mahdl.compiler;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.common.processor.ErrorHandler;
import name.martingeisse.mahdl.common.processor.ModuleProcessor;
import name.martingeisse.mahdl.common.processor.definition.ModuleDefinition;
import name.martingeisse.mahdl.compiler.codegen.CodeGenerator;
import name.martingeisse.mahdl.compiler.model.GenerationModel;
import name.martingeisse.mahdl.input.FlexGeneratedMahdlLexer;
import name.martingeisse.mahdl.input.MapagGeneratedMahdlParser;
import name.martingeisse.mahdl.input.Symbols;
import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import name.martingeisse.mahdl.input.cm.impl.CmTokenImpl;
import name.martingeisse.mahdl.input.cm.impl.IElementType;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleCompiler {

    private static final ErrorHandler moduleProcessorErrorHandler = new ErrorHandler() {

        @Override
        public void onError(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
            CompilerContext.get().reportError(errorSource, message, t);
        }

        @Override
        public void onDiagnostic(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
            CompilerContext.get().reportDiagnostic(errorSource, message, t);
        }

    };

    private final CompilerContext context;
    private ImmutableMap<ModuleIdentifier, ModuleWrapper> sourceModules;
    private ImmutableMap<ModuleIdentifier, ModuleApi> sourceModuleApis;

    public ModuleCompiler(CompilerContext context) {
        this.context = context;
    }

    public void compile() throws IOException {
        CompilerEnvironment.initialize();
        CompilerContextHolder.set(context);
        try {
            sourceModules = readSources();
            processSourceModuleApis();
            for (ModuleWrapper moduleWrapper : sourceModules.values()) {
                compile(moduleWrapper);
            }
        } finally {
            CompilerContextHolder.remove();
        }
    }

    private ImmutableMap<ModuleIdentifier, ModuleWrapper> readSources() throws IOException {
        Map<ModuleIdentifier, ModuleWrapper> result = new HashMap<>();
        for (InputModuleEntry inputModuleEntry : context.getInputModuleEntries()) {
            ModuleWrapper moduleWrapper;
            try (InputStream inputStream = inputModuleEntry.getLoader().load()) {
                moduleWrapper = readSource(inputModuleEntry.getIdentifier(), inputStream);
            }
            result.put(moduleWrapper.getIdentifier(), moduleWrapper);
        }
        return ImmutableMap.copyOf(result);
    }

    // returns null if reading the source file fails hard
    private ModuleWrapper readSource(ModuleIdentifier moduleIdentifier, InputStream in) throws IOException {

        // Run the lexer.
        // JFlex generates a lexer that takes a reader but does not read from it, so pass the content manually.
        String textContent = IOUtils.toString(in, StandardCharsets.UTF_8);
        FlexGeneratedMahdlLexer lexer = new FlexGeneratedMahdlLexer(null);
        lexer.reset(textContent, 0, textContent.length(), FlexGeneratedMahdlLexer.YYINITIAL);
        List<CmTokenImpl> tokens = new ArrayList<>();
        while (true) {
            IElementType elementType = lexer.advance();
            if (elementType == null) {
                break;
            }
            if (elementType != IElementType.WHITE_SPACE && elementType != Symbols.LINE_COMMENT && elementType != Symbols.BLOCK_COMMENT) {
                tokens.add(new CmTokenImpl(lexer.yyline + 1, lexer.yycolumn + 1, lexer.yytext().toString(), elementType));
            }
        }
        if (tokens.isEmpty()) {
            CompilerContext.get().reportError(moduleIdentifier, 0, 0, "empty source file");
            return null;
        }

        // run parser
        MapagGeneratedMahdlParser parser = new MapagGeneratedMahdlParser() {
            @Override
            protected void reportError(CmTokenImpl locationToken, String message) {
                int row = locationToken.getRow();
                int column = locationToken.getColumn();
                CompilerContext.get().reportError(moduleIdentifier, row, column, message);
            }
        };
        CmNode rootNode = parser.parse(tokens.toArray(new CmTokenImpl[0]));
        if (rootNode == null) {
            return null;
        }

        // check root node type
        if (rootNode instanceof Module) {
            return new ModuleWrapper(moduleIdentifier, (Module) rootNode, this);
        } else {
            CompilerContext.get().reportError(moduleIdentifier, 0, 0, "wrong root CM node: " + rootNode);
            return null;
        }

    }

    private void processSourceModuleApis() {
        Map<ModuleIdentifier, ModuleApi> sourceModuleApis = new HashMap<>();
        for (ModuleWrapper moduleWrapper : sourceModules.values()) {
            sourceModuleApis.put(moduleWrapper.getIdentifier(), new ModuleApi(moduleWrapper.getModule()));
        }
        this.sourceModuleApis = ImmutableMap.copyOf(sourceModuleApis);
    }

    private void compile(ModuleWrapper moduleWrapper) throws IOException {

        // these names are used in the generated Java code
        ModuleIdentifier moduleIdentifier = moduleWrapper.getIdentifier();
        String qualifiedName = moduleIdentifier.toString();
        String packageName = moduleIdentifier.packageToString();
        String localName = moduleIdentifier.localNameToString();

        // used by the code generator to generate output data files -- we have to map this to our CompilerContext
        CodeGenerator.DataFileFactory dataFileFactory = new CodeGenerator.DataFileFactory() {

            @Override
            public String getAnchorClassName() {
                return qualifiedName;
            }

            @Override
            public void createDataFile(String filename, byte[] data) throws IOException {
                try (OutputStream out = context.openDataOutputFile(moduleIdentifier, filename)) {
                    IOUtils.write(data, out);
                }
            }

        };

        // actually compile the module
        ModuleDefinition moduleDefinition = new ModuleProcessor(moduleWrapper.getModule(), moduleProcessorErrorHandler).process();
        GenerationModel model = new GenerationModel(moduleDefinition, packageName, localName);
        CodeGenerator codeGenerator = new CodeGenerator(model, dataFileFactory, moduleProcessorErrorHandler);
        codeGenerator.run();
        try (OutputStream out = context.openCodeOutputFile(moduleIdentifier)) {
            IOUtils.write(codeGenerator.getCode(), out, StandardCharsets.UTF_8);
        }
        context.writeOutputModuleApi(moduleIdentifier, new ModuleApi(moduleDefinition));

    }

    public ModuleApi getModuleApi(QualifiedModuleName name) throws ReferenceResolutionException {
        ModuleIdentifier moduleIdentifier = new ModuleIdentifier(name);
        ModuleApi api = sourceModuleApis.get(moduleIdentifier);
        if (api == null) {
            throw new ReferenceResolutionException("cannot resolve module " + moduleIdentifier);
        }
        return api;
    }

}
