package name.martingeisse.mahdl.gradle;

import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.compiler.CompilerConstants;
import name.martingeisse.mahdl.gradle.json.ModuleApiGson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Stores dependency information on a per-JAR-file basis.
 */
final class DependencyCache {

    private final Map<File, FileEntry> fileEntries = new HashMap<>();

    ModuleApi get(File jarFile, ModuleIdentifier identifier) throws IOException {
        FileEntry fileEntry = fileEntries.get(jarFile);
        if (fileEntry == null) {
            fileEntry = new FileEntry(jarFile);
            fileEntries.put(jarFile, fileEntry);
        }
        return fileEntry.get(identifier);
    }

    private static final class FileEntry {

        private final Map<ModuleIdentifier, ModuleApi> moduleApis = new HashMap<>();

        FileEntry(File jarFile) throws IOException {
            try (ZipFile zipFile = new ZipFile(jarFile)) {
                Enumeration<? extends ZipEntry> zipEntryEnumeration = zipFile.entries();
                while (zipEntryEnumeration.hasMoreElements()) {
                    handleZipEntry(zipFile, zipEntryEnumeration.nextElement());
                }
            }
        }

        private void handleZipEntry(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
            System.out.println(zipEntry.getName());
            if (zipEntry.getName().endsWith(CompilerConstants.JSON_FILENAME_SUFFIX)) {
                String baseFilename = zipEntry.getName().substring(0, zipEntry.getName().length() -
                        CompilerConstants.JSON_FILENAME_SUFFIX.length());
                ModuleIdentifier moduleIdentifier = new ModuleIdentifier(baseFilename.replace('/', '.'));
                ModuleApi moduleApi;
                try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                    try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                        moduleApi = ModuleApiGson.gson.fromJson(inputStreamReader, ModuleApi.class);
                    }
                }
                moduleApis.put(moduleIdentifier, moduleApi);
            }
        }

        ModuleApi get(ModuleIdentifier identifier) {
            return moduleApis.get(identifier);
        }

    }

}
