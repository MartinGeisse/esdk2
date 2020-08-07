package name.martingeisse.mahdl.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 *
 */
class MahdlCodegenTask extends DefaultTask {

	File sourceDirectory;

	File outputDirectory;

	@TaskAction
	void run() {
		CompilationRun compilationRun = new CompilationRun(sourceDirectory, outputDirectory);
		compilationRun.run();
	}

}
