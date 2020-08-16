package name.martingeisse.digiscope

import com.google.common.collect.ImmutableList
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

/**
 *
 */
class DigiscopeCodegenTask extends DefaultTask {

    @OutputDirectory
    File outputDirectory = "$buildDir/digiscopeJava";

    @TaskAction
    void run() {
        System.out.println(outputDirectory.getCanonicalPath());
    }

}
