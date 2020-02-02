package name.martingeisse.mahdl.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Debugging: ./gradlew clean build -Dorg.gradle.debug=true --no-daemon
 *
 * To view MaHDL codegen errors in IntelliJ, delegate build actions to Gradle, then in the gradle view, click the
 * "toggle view" icon on the left-hand toolbar. This makes the textual gradle output with all messages visible.
 * Without toggling, IntelliJ tries to map errors back to Java sources which does not work for MaHDL sources.
 */
class MahdlGradlePlugin implements Plugin<Project> {

	void apply(Project project) {
		MahdlCodegenTask task = project.tasks.create("mahdlCodegen", MahdlCodegenTask.class);
		task.group = 'build';
		task.description = 'Generates Java code from MaHDL sources.';
		task.sourceDirectory = new File(project.projectDir, "src/mahdl");
		task.outputDirectory = new File(project.getBuildDir(), "mahdl-java");
		project.tasks.compileJava.dependsOn(task);
		project.sourceSets.main.java.srcDirs += task.outputDirectory;
		project.sourceSets.main.resources.srcDirs += task.outputDirectory;
		project.sourceSets.test.java.srcDirs += task.outputDirectory;
		project.sourceSets.test.resources.srcDirs += task.outputDirectory;
	}

}
