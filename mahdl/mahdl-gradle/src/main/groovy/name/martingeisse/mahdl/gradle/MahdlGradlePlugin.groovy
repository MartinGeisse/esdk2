package name.martingeisse.mahdl.gradle


import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.invocation.Gradle

/**
 * Debugging: ./gradlew clean build -Dorg.gradle.debug=true --no-daemon
 *
 * To view MaHDL codegen errors in IntelliJ, delegate build actions to Gradle, then in the gradle view, click the
 * "toggle view" icon on the left-hand toolbar. This makes the textual gradle output with all messages visible.
 * Without toggling, IntelliJ tries to map errors back to Java sources which does not work for MaHDL sources.
 */
class MahdlGradlePlugin implements Plugin<Project> {

    void apply(Project project) {

        // create the mahdlCodegen task
        MahdlCodegenTask task = project.tasks.create('mahdlCodegen', MahdlCodegenTask.class);
        task.group = 'build';
        task.description = 'Generates Java code from MaHDL sources.';
        task.sourceDirectory = new File(project.projectDir, "src/mahdl");
        if (!task.sourceDirectory.isDirectory()) {
            task.sourceDirectory = null;
        }
        task.outputDirectory = new File(project.buildDir, "mahdl-java");

        // mahdlCodegen must run before compiling Java code
        project.tasks.compileJava.dependsOn(task);

        // add the task's output folders as Java source folders
        project.sourceSets.main.java.srcDirs += task.outputDirectory;
        project.sourceSets.main.resources.srcDirs += task.outputDirectory;
        project.sourceSets.test.java.srcDirs += task.outputDirectory;
        project.sourceSets.test.resources.srcDirs += task.outputDirectory;

        // Turn project dependencies into task dependencies, and add their JAR files as well as external JAR
        // dependencies as inputs to our task. We have to delay this until the end of the configuration
        // phase because project dependencies are not fully known until then.
        project.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsEvaluated(Gradle gradle) {
                project.configurations.each { configuration ->
                    configuration.dependencies.each { dependency ->
                        task.dependencyOutputs += dependency
                        if (dependency instanceof ProjectDependency) {
                            def producerProject = ((ProjectDependency) dependency).dependencyProject
                            if (producerProject.tasks.hasProperty("jar")) {
                                task.dependsOn(producerProject.tasks.jar)
                            }
                        }
                    }
                }
            }
        });

    }

}
