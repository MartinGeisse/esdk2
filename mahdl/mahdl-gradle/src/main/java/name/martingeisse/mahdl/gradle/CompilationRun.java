package name.martingeisse.mahdl.gradle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.compiler.CompilerContextHolder;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *
 */
public class CompilationRun {

	private final File mahdlDir;
	private final File javaDir;
	private ImmutableMap<ImmutableList<String>, ModuleWrapper> codeModels = null;
	private ImmutableMap<ImmutableList<String>, File> dataFiles = null;

	public CompilationRun(File mahdlDir, File javaDir) {
		this.mahdlDir = mahdlDir;
		this.javaDir = javaDir;
	}

	public void run() throws IOException {
		try {
			CompilerContextHolder.set(new TempCompilerContext());
			runInternal();
			CompilationErrors.failOnErrors();
		} finally {
			CompilationErrors.clearErrors();
			CompilerContextHolder.remove();
		}
	}

	private void runInternal() throws IOException {
		GradleEnvironment.initialize();

		// clear old build results
		FileUtils.deleteDirectory(javaDir);
		if (!javaDir.mkdirs()) {
			throw new IOException("could not create output directory " + javaDir);
		}

		// load sources
		SourceLoader sourceLoader = new SourceLoader(mahdlDir);
		sourceLoader.run();
		this.codeModels = sourceLoader.getCodeModels();
		this.dataFiles = sourceLoader.getDataFiles();

		// process sources
		ProcessingRun processingRun = new ProcessingRun(codeModels, dataFiles);
		processingRun.run();

		// write output
		for (Map.Entry<ImmutableList<String>, String> javaEntry : processingRun.getGeneratedCode().entrySet()) {
			File outputFile = prepareOutputFile(javaEntry.getKey(), ".java");
			FileUtils.writeStringToFile(outputFile, javaEntry.getValue(), StandardCharsets.UTF_8);
		}
		for (Map.Entry<ImmutableList<String>, byte[]> dataFileEntry : processingRun.getOutputDataFiles().entrySet()) {
			File outputFile = prepareOutputFile(dataFileEntry.getKey(), "");
			FileUtils.writeByteArrayToFile(outputFile, dataFileEntry.getValue());
		}

	}

	private File prepareOutputFile(ImmutableList<String> qualifiedName, String filenameSuffix) throws IOException {
		if (qualifiedName.isEmpty()) {
			throw new IllegalArgumentException("cannot handle empty qualified name");
		}
		File folder = prepareOutputFolder(javaDir, qualifiedName, 0);
		return new File(folder, qualifiedName.get(qualifiedName.size() - 1) + filenameSuffix);
	}

	private File prepareOutputFolder(File currentFolder, ImmutableList<String> qualifiedName, int skip) throws IOException {
		if (skip == qualifiedName.size() - 1) {
			return currentFolder;
		}
		String segment = qualifiedName.get(skip);
		File subfolder = new File(currentFolder, segment);
		if (!subfolder.isDirectory()) {
			if (!subfolder.mkdir()) {
				throw new IOException("could not create output folder " + subfolder);
			}
		}
		return prepareOutputFolder(subfolder, qualifiedName, skip + 1);
	}

}
