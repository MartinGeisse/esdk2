package name.martingeisse.mahdl.gradle;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.compiler.CompilerContext;
import name.martingeisse.mahdl.compiler.InputModuleEntry;
import name.martingeisse.mahdl.compiler.ModuleCompiler;
import name.martingeisse.mahdl.compiler.util.FileInputCollector;
import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.impl.CmNodeImpl;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Wraps the actual compiler and does gradle-specific things. This class mainly exists to keep as much code as possible
 * in Java instead of Groovy.
 */
public final class CompilerAdapter implements CompilerContext {

    private static final boolean ENABLE_DIAGNOSTICS = false;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final ImmutableList<File> sourceDirectories;
    private final ImmutableList<File> dependencyOutputs;
    private final File outputDirectory;
    private ImmutableList<InputModuleEntry> inputModuleEntries;
    private boolean hasErrors = false;

    public CompilerAdapter(ImmutableList<File> sourceDirectories, ImmutableList<File> dependencyOutputs, File outputDirectory) {
        this.sourceDirectories = sourceDirectories;
        this.dependencyOutputs = dependencyOutputs;
        this.outputDirectory = outputDirectory;
    }

    public void run() throws IOException {

        // clear old build results
        FileUtils.deleteDirectory(outputDirectory);
        if (!outputDirectory.mkdirs()) {
            throw new IOException("could not create output directory " + outputDirectory);
        }

        inputModuleEntries = FileInputCollector.collect(sourceDirectories);
        ModuleCompiler compiler = new ModuleCompiler(this);
        compiler.compile();
        if (hasErrors) {
            throw new CompilationFailedException("Compilation failed; see the compiler error output for details.");
        }
    }

    //region error handling

    private static void printDiagnosticPrefix() {
        System.err.println("DIAGNOSTIC:");
    }


    @Override
    public void reportDiagnostic(CmLinked errorSource, String message, Throwable exception) {
        if (ENABLE_DIAGNOSTICS) {
            printDiagnosticPrefix();
            reportError(errorSource, message, exception);
        }
    }

    @Override
    public void reportDiagnostic(ModuleIdentifier moduleIdentifier, int row, int column, String message, Throwable exception) {
        if (ENABLE_DIAGNOSTICS) {
            printDiagnosticPrefix();
            reportError(moduleIdentifier, row, column, message, exception);
        }
    }

    @Override
    public void reportError(CmLinked errorSource, String message, Throwable exception) {
        ModuleIdentifier moduleIdentifier = null;
        int row = 0, column = 0;
        if (errorSource != null) {
            CmNodeImpl nodeImpl = (CmNodeImpl) errorSource.getCmNode();
            row = nodeImpl.getRow();
            column = nodeImpl.getColumn();
            ModuleWrapper moduleWrapper = ModuleWrapper.get(nodeImpl);
            if (moduleWrapper != null) {
                moduleIdentifier = moduleWrapper.getIdentifier();
            }
        }
        reportError(moduleIdentifier, row, column, message, exception);
    }

    @Override
    public void reportError(ModuleIdentifier moduleIdentifier, int row, int column, String message, Throwable exception) {
        hasErrors = true;
        String moduleIdentifierText = (moduleIdentifier == null ? "<unknown module>" : moduleIdentifier.toString());
        System.err.println(moduleIdentifierText + ':' + row + ": error: " + message);
        if (exception != null) {
            exception.printStackTrace(System.err);
        }
    }

    //endregion
    //region other CompilerContext methods
    //endregion

    @Override
    public ImmutableCollection<InputModuleEntry> getInputModuleEntries() {
        return inputModuleEntries;
    }

    @Override
    public ModuleApi readDependencyModuleApi(ModuleIdentifier identifier) throws IOException {
        ModuleApi result = null;
        for (File dependencyOutput : dependencyOutputs) {
            if (dependencyOutput.isFile()) {
                if (!dependencyOutput.getName().endsWith(".jar")) {
                    throw new IOException("don't know how to read dependency: " + dependencyOutput);
                }
                // TODO JAR file
            } else if (dependencyOutput.isDirectory()) {
                // TODO class file folder
                /*
                File file = new File(baseFolder, identifier.toString().replace('.', '/') + ".json");
                return (file.exists() ? new FileInputStream(file) : null);
                 */
            } else if (dependencyOutput.exists()) {
                throw new IOException("dependency is neither a directory nor a file: " + dependencyOutput);
            } else {
                throw new IOException("dependency does not exist: " + dependencyOutput);
            }
        }
        if (result == null) {
            throw new IOException("metadata JSON file not found for " + identifier);
        }
        return result;
    }

    @Override
    public InputStream openDataInputFile(ModuleIdentifier anchorIdentifier, String filename) throws IOException {
        File file = null;
        for (File sourceDirectory : sourceDirectories) {
            File testFile = new File(sourceDirectory, anchorIdentifier.packageToString('/') + '/' + filename);
            if (testFile.exists()) {
                if (file != null) {
                    throw new IOException("ambiguous data file name: " + anchorIdentifier + " / " + filename);
                }
                file = testFile;
            }
        }
        if (file == null) {
            throw new IOException("data file not found: " + anchorIdentifier + " / " + filename);
        }
        return new FileInputStream(file);
    }

    @Override
    public void writeOutputModuleApi(ModuleIdentifier identifier, ModuleApi moduleApi) throws IOException {
        File file = new File(outputDirectory, identifier.toString('/') + ".json");
        FileUtils.forceMkdir(file.getParentFile());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
                gson.toJson(moduleApi, outputStreamWriter);
            }
        }
    }

    @Override
    public OutputStream openCodeOutputFile(ModuleIdentifier identifier) throws IOException {
        File file = new File(outputDirectory, identifier.toString('/') + ".java");
        FileUtils.forceMkdir(file.getParentFile());
        return new FileOutputStream(file);
    }

    @Override
    public OutputStream openDataOutputFile(ModuleIdentifier anchorIdentifier, String filename) throws IOException {
        File file = new File(outputDirectory, anchorIdentifier.packageToString('/') + '/' + filename);
        FileUtils.forceMkdir(file.getParentFile());
        return new FileOutputStream(file);
    }

}
