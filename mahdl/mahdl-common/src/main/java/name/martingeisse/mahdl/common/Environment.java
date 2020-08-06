package name.martingeisse.mahdl.common;

import name.martingeisse.mahdl.common.processor.ModuleProcessor;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implements the necessary behavior that is specific to the environment in which the MaHDL logic is embedded
 * (IntelliJ, Gradle, Maven, ...). This is a singleton and as such, MUST NOT contain any state. In particular,
 * project-specific state that is needed to resolve module references must be kept outside this class, since the
 * singleton may be used for multiple projects in parallel.
 *
 * To implement project-specific state, add it to the CM. For example, an environment-specific CM root node can be
 * added on top of the {@link Module} node that contains project-specific state. The Environment singleton should then
 * only contain the logic to access that data.
 *
 * We do not properly inject the environment into the {@link ModuleProcessor} to simplify the code, based on the
 * assumption that it won't run in an environment where multiple actual environments (e.g. IntelliJ AND Gradle) run in
 * the same JVM.
 */
public interface Environment {

	InputStream openDataFile(CmNode anchor, String filename) throws IOException;

	void validateModuleNameAgainstFilePath(Module module, QualifiedModuleName name) throws IOException;

	ModuleApi getModuleApi(QualifiedModuleName name) throws ReferenceResolutionException;

	class Holder {
		public static Environment INSTANCE = null;
	}

}
