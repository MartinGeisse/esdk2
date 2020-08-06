package name.martingeisse.mahdl.gradle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.common.processor.ErrorHandler;
import name.martingeisse.mahdl.common.processor.ModuleProcessor;
import name.martingeisse.mahdl.common.processor.definition.ModuleDefinition;
import name.martingeisse.mahdl.gradle.codegen.CodeGenerator;
import name.martingeisse.mahdl.gradle.model.GenerationModel;
import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ProcessingRun {

	private ImmutableMap<ImmutableList<String>, ModuleWrapper> codeModels;
	private ImmutableMap<ModuleWrapper, ImmutableList<String>> codeModelsReverse;
	private ImmutableMap<ImmutableList<String>, File> inputDataFiles;
	private Map<ImmutableList<String>, String> generatedCode;
	private Map<ImmutableList<String>, byte[]> outputDataFiles;

	public ProcessingRun(ImmutableMap<ImmutableList<String>, ModuleWrapper> codeModels, ImmutableMap<ImmutableList<String>, File> dataFiles) {
		this.codeModels = codeModels;
		this.codeModelsReverse = ImmutableMap.copyOf(reverse(codeModels));
		this.inputDataFiles = dataFiles;
	}

	private static final <A, B> Map<B, A> reverse(Map<A, B> map) {
		Map<B, A> result = new HashMap<>();
		for (Map.Entry<A, B> entry : map.entrySet()) {
			result.put(entry.getValue(), entry.getKey());
		}
		return result;
	}

	public ImmutableMap<ImmutableList<String>, String> getGeneratedCode() {
		return ImmutableMap.copyOf(generatedCode);
	}

	public Map<ImmutableList<String>, byte[]> getOutputDataFiles() {
		return outputDataFiles;
	}

	public void run() {
		generatedCode = new HashMap<>();
		outputDataFiles = new HashMap<>();
		for (Map.Entry<ImmutableList<String>, ModuleWrapper> codeModelEntry : codeModels.entrySet()) {
			ImmutableList<String> qualifiedName = codeModelEntry.getKey();
			ModuleWrapper moduleWrapper = codeModelEntry.getValue();
			try {
				moduleWrapper.setProcessingRun(this);
				Module moduleCm = moduleWrapper.getModule();
				ErrorHandler errorHandler = new ErrorHandler() {

					@Override
					public void onError(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
						CompilationErrors.reportError(errorSource, message, t);
					}

					@Override
					public void onDiagnostic(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
						CompilationErrors.reportDiagnostic(errorSource, message, t);
					}

				};
				ModuleDefinition moduleDefinition = new ModuleProcessor(moduleCm, errorHandler).process();
				String packageName = StringUtils.join(qualifiedName.subList(0, qualifiedName.size() - 1), '.');
				String localName = qualifiedName.get(qualifiedName.size() - 1);
				GenerationModel model = new GenerationModel(moduleDefinition, packageName, localName);
				CodeGenerator.DataFileFactory dataFileFactory = new CodeGenerator.DataFileFactory() {

					@Override
					public String getAnchorClassName() {
						return packageName + '.' + localName;
					}

					@Override
					public void createDataFile(String filename, byte[] data) {
						List<String> qualifiedDataFileName = new ArrayList<>(qualifiedName.subList(0, qualifiedName.size() - 1));
						qualifiedDataFileName.add(filename);
						outputDataFiles.put(ImmutableList.copyOf(qualifiedDataFileName), data);
					}

				};
				CodeGenerator codeGenerator = new CodeGenerator(model, dataFileFactory, errorHandler);
				codeGenerator.run();
				generatedCode.put(qualifiedName, codeGenerator.getCode());
			} finally {
				moduleWrapper.setProcessingRun(null);
			}
		}
	}

	public InputStream openInputDataFile(ModuleWrapper moduleWrapper, String filename) throws IOException {
		ImmutableList<String> qualifiedModuleName = codeModelsReverse.get(moduleWrapper);
		ImmutableList<String> qualifiedFileName = replaceLast(qualifiedModuleName, filename);
		File dataFile = inputDataFiles.get(qualifiedFileName);
		if (dataFile == null) {
			throw new FileNotFoundException(filename);
		}
		return new FileInputStream(dataFile);
	}

	public void validateModuleNameAgainstFilePath(ModuleWrapper moduleWrapper, QualifiedModuleName name) throws IOException {
		ImmutableList<String> moduleNameFromFilename = codeModelsReverse.get(moduleWrapper);
		ImmutableList<String> moduleNameFromContents = convertQualifiedModuleName(name);
		if (!moduleNameFromContents.equals(moduleNameFromFilename)) {
			throw new IOException("expected module " + toString(moduleNameFromFilename) + " in this file, found " + toString(moduleNameFromContents));
		}
	}

	public Module resolveModuleReference(ModuleWrapper moduleWrapper, QualifiedModuleName name) throws ReferenceResolutionException {
		ImmutableList<String> segments = convertQualifiedModuleName(name);
		ModuleWrapper resolved = codeModels.get(segments);
		if (resolved == null) {
			throw new ReferenceResolutionException("unknown module: " + toString(segments));
		}
		return resolved.getModule();
	}

	// helper methods below

	private ImmutableList<String> replaceLast(ImmutableList<String> list, String segment) {
		List<String> result = new ArrayList<>(list);
		result.set(result.size() - 1, segment);
		return ImmutableList.copyOf(result);
	}

	private ImmutableList<String> convertQualifiedModuleName(QualifiedModuleName qualifiedModuleName) {
		List<String> segments = new ArrayList<>();
		for (CmToken token : qualifiedModuleName.getSegments().getAll()) {
			segments.add(token.getText());
		}
		return ImmutableList.copyOf(segments);
	}

	private String toString(ImmutableList<String> segments) {
		return StringUtils.join(segments, '.');
	}

}
