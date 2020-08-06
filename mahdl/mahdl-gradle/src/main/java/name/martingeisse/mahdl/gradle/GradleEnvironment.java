package name.martingeisse.mahdl.gradle;

import name.martingeisse.mahdl.common.Environment;
import name.martingeisse.mahdl.common.ModuleApi;
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
public class GradleEnvironment implements Environment {

	public static void initialize() {
		if (Holder.INSTANCE == null) {
			Holder.INSTANCE = new GradleEnvironment();
		}
	}

	@Override
	public InputStream openDataFile(CmNode anchor, String filename) throws IOException {
		ModuleWrapper moduleWrapper = ModuleWrapper.get(anchor);
		if (moduleWrapper == null) {
			throw new IOException("could not locate data file due to previous errors");
		}
		return moduleWrapper.getProcessingRun().openInputDataFile(moduleWrapper, filename);
	}

	@Override
	public void validateModuleNameAgainstFilePath(Module module, QualifiedModuleName name) throws IOException {
		ModuleWrapper moduleWrapper = ModuleWrapper.get(module);
		if (moduleWrapper == null) {
			throw new IOException("could not validate module name due to previous errors");
		}
		moduleWrapper.getProcessingRun().validateModuleNameAgainstFilePath(moduleWrapper, name);
	}

	@Override
	public ModuleApi getModuleApi(QualifiedModuleName name) throws ReferenceResolutionException {
		ModuleWrapper moduleWrapper = ModuleWrapper.get(name);
		if (moduleWrapper == null) {
			throw new ReferenceResolutionException("could not resolve module reference due to previous errors");
		}
		Module module = moduleWrapper.getProcessingRun().resolveModuleReference(moduleWrapper, name);
		return new ModuleApi(module);
	}

}
