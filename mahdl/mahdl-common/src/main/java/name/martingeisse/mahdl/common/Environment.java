package name.martingeisse.mahdl.common;

import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public interface Environment {

	InputStream openDataFile(CmNode anchor, String filename) throws IOException;

	void validateModuleNameAgainstFilePath(Module module, QualifiedModuleName name) throws IOException;

	Module resolveModuleReference(QualifiedModuleName name) throws ReferenceResolutionException;

	default ModuleApi getModuleApi(QualifiedModuleName name) throws ReferenceResolutionException {
		return new ModuleApi(resolveModuleReference(name));
	}

	class Holder {
		public static Environment INSTANCE = null;
	}

}
