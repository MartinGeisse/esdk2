package name.martingeisse.mahdl.compiler.util;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.compiler.InputModuleEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FileInputCollector {

    private FileInputCollector() {
    }

    public static ImmutableList<InputModuleEntry> collect(ImmutableCollection<File> sourceFolders) throws IOException {
        if (sourceFolders == null) {
            throw new IllegalArgumentException("sourceFolders is null");
        }
        return collect(sourceFolders.toArray(new File[0]));
    }

    public static ImmutableList<InputModuleEntry> collect(File... sourceFolders) throws IOException {
        if (sourceFolders == null) {
            throw new IllegalArgumentException("sourceFolders is null");
        }
        List<InputModuleEntry> result = new ArrayList<>();
        for (File sourceFolder : sourceFolders) {
            if (sourceFolder == null) {
                throw new IllegalArgumentException("sourceFolders contains a null entry");
            }
            if (!sourceFolder.isDirectory()) {
                throw new IllegalArgumentException("source folder " + sourceFolder + " is not a directory");
            }
            collect(sourceFolder, new ArrayList<>(), result);
        }
        return ImmutableList.copyOf(result);
    }

    private static void collect(File sourceFolder, List<String> nameSegments, List<InputModuleEntry> accumulator) throws IOException {
        File[] files = sourceFolder.listFiles();
        if (files == null) {
            throw new IOException("cannot list files for " + sourceFolder);
        }
        for (File file : files) {
            if (file.getName().endsWith(".mahdl")) {

                // source file
                String baseName = file.getName().substring(0, file.getName().length() - ".mahdl".length());
                if (!ModuleIdentifier.isValidSegment(baseName)) {
                    throw new IOException("found module file with invalid name: " + file);
                }
                nameSegments.add(baseName);
                ModuleIdentifier identifier = new ModuleIdentifier(nameSegments);
                nameSegments.remove(nameSegments.size() - 1);
                InputModuleEntry.Loader loader = () -> new FileInputStream(file);
                accumulator.add(new InputModuleEntry(identifier, loader));

            } else if (file.isDirectory()) {

                // sub-package
                if (!ModuleIdentifier.isValidSegment(file.getName())) {
                    throw new IOException("found sub-package with invalid name: " + file);
                }
                nameSegments.add(file.getName());
                collect(file, nameSegments, accumulator);
                nameSegments.remove(nameSegments.size() - 1);

            }
        }
    }

}
