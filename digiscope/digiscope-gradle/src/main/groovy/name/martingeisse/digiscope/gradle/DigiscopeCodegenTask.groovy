package name.martingeisse.digiscope.gradle


import org.gradle.BuildAdapter
import org.gradle.api.DefaultTask
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 *
 */
class DigiscopeCodegenTask extends DefaultTask {

    @Input
    String className;

    @OutputDirectory
    @Optional
    File outputDirectory;

    DigiscopeCodegenTask() {
        outputDirectory = new File(project.buildDir, "digiscopeJava");
        project.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsEvaluated(Gradle gradle) {
                project.tasks.compileJava.dependsOn DigiscopeCodegenTask.this
                project.sourceSets.main.java.srcDirs += outputDirectory;
            }
        });
    }

    @TaskAction
    void run() throws Exception {
        new CodeGenerator(this).run();
    }

}
