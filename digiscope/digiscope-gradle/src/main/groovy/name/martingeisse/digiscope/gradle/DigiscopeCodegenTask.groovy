package name.martingeisse.digiscope.gradle

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.BuildAdapter
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 *
 */
class DigiscopeCodegenTask extends DefaultTask {

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
    void run() {

        // generate output
        File testFolder = new File(outputDirectory, "testgen");
        File testFile = new File(testFolder, "TestGen.java");
        FileUtils.write(testFile, "package testgen; public class TestGen { public static void foo() {System.out.println(\"Hello generated world!\");} }\n");

        // TODO remove
        System.out.println(project.sourceSets.main.java.srcDirs);

    }

}
