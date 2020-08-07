package name.martingeisse.mahdl.compiler;

import name.martingeisse.mahdl.common.Environment;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class CompilerEnvironment implements Environment {

    public static void initialize() {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new CompilerEnvironment();
        }
    }

    @Override
    public InputStream openDataFile(CmNode anchor, String filename) throws IOException {
        ModuleWrapper moduleWrapper = ModuleWrapper.get(anchor);
        if (moduleWrapper == null) {
            throw new IOException("could not locate data file due to previous errors");
        }
        return CompilerContext.get().openDataInputFile(moduleWrapper.getIdentifier(), filename);
    }

    @Override
    public void validateModuleNameAgainstFilePath(Module module, QualifiedModuleName name) throws IOException {
        ModuleWrapper moduleWrapper = ModuleWrapper.get(module);
        if (moduleWrapper == null) {
            throw new IOException("could not validate module name due to previous errors");
        }
        ModuleIdentifier expectedIdentifier = moduleWrapper.getIdentifier();
        ModuleIdentifier specifiedIdentifier = new ModuleIdentifier(name);
        if (!moduleWrapper.getIdentifier().equals(specifiedIdentifier)) {
            throw new IOException("expected module " + expectedIdentifier + " in this file, found " + specifiedIdentifier);
        }
    }

    @Override
    public ModuleApi getModuleApi(QualifiedModuleName name) throws ReferenceResolutionException, IOException {
        ModuleWrapper moduleWrapper = ModuleWrapper.get(name);
        return moduleWrapper.getCompiler().getModuleApi(name);
    }

}
