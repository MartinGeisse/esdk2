package name.martingeisse.mahdl.gradle

import com.google.common.collect.ImmutableList
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 *
 */
class MahdlCodegenTask extends DefaultTask {

    @InputDirectory
    File sourceDirectory;

    @InputFiles
    List<File> dependencyOutputs = new ArrayList<>();

    @OutputDirectory
    File outputDirectory;

    @TaskAction
    void run() {
        CompilerAdapter adapter = new CompilerAdapter(ImmutableList.of(sourceDirectory),
                ImmutableList.copyOf(dependencyOutputs), outputDirectory);
        adapter.run();
    }

}
