package name.martingeisse.mahdl.gradle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.input.FlexGeneratedMahdlLexer;
import name.martingeisse.mahdl.input.MapagGeneratedMahdlParser;
import name.martingeisse.mahdl.input.Symbols;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Module;
import name.martingeisse.mahdl.input.cm.impl.CmTokenImpl;
import name.martingeisse.mahdl.input.cm.impl.IElementType;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class SourceLoader {

	private static final String MAHDL_FILENAME_SUFFIX = ".mahdl";

	private final File mahdlDir;
	private final Map<ImmutableList<String>, ModuleWrapper> codeModels = new HashMap<>();
	private final Map<ImmutableList<String>, File> dataFiles = new HashMap<>();
	private final List<String> segments = new ArrayList<>();

	public SourceLoader(File mahdlDir) {
		this.mahdlDir = mahdlDir;
	}

	public ImmutableMap<ImmutableList<String>, ModuleWrapper> getCodeModels() {
		return ImmutableMap.copyOf(codeModels);
	}

	public ImmutableMap<ImmutableList<String>, File> getDataFiles() {
		return ImmutableMap.copyOf(dataFiles);
	}

	public void run() {
		segments.clear();
		loadSources(mahdlDir, new ArrayList<>());
	}

	private void loadSources(File mahdlPackageDir, List<String> segments) {
		File[] entries = mahdlPackageDir.listFiles();
		if (entries == null) {
			return;
		}
		for (File entry : entries) {
			if (entry.isDirectory()) {
				if (entry.getName().contains(".")) {
					CompilationErrors.reportError(entry.getPath(), 0, 0, "a package folder cannot have a dot in its name");
				} else {
					pushSegment(entry.getName());
					loadSources(entry, segments);
					popSegment();
				}
			} else if (entry.isFile()) {
				String filename = entry.getName();
				if (filename.endsWith(MAHDL_FILENAME_SUFFIX)) {
					String moduleName = filename.substring(0, filename.length() - MAHDL_FILENAME_SUFFIX.length());
					Module codeModel = loadSourceFile(entry);
					if (codeModel != null) {
						codeModels.put(buildName(moduleName), new ModuleWrapper(entry, codeModel));
					}
				} else {
					dataFiles.put(buildName(filename), entry);
				}
			}
		}
	}

	private ImmutableList<String> buildName(String localName) {
		pushSegment(localName);
		ImmutableList<String> fullName = ImmutableList.copyOf(segments);
		popSegment();
		return fullName;
	}

	private void pushSegment(String segment) {
		segments.add(segment);
	}

	private void popSegment() {
		segments.remove(segments.size() - 1);
	}

	private Module loadSourceFile(File file) {
		try {
			// JFlex generates a lexer that takes a reader but does not read from it, so pass the content manually.
			String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			return loadSourceFile(file.getPath(), content);
		} catch (IOException e) {
			CompilationErrors.reportError(file.getPath(), 0, 0, e.toString());
			return null;
		}
	}

	private Module loadSourceFile(String path, String content) throws IOException {

		// run lexer
		FlexGeneratedMahdlLexer lexer = new FlexGeneratedMahdlLexer(null);
		lexer.reset(content, 0, content.length(), FlexGeneratedMahdlLexer.YYINITIAL);
		List<CmTokenImpl> tokens = new ArrayList<>();
		while (true) {
			IElementType elementType = lexer.advance();
			if (elementType == null) {
				break;
			}
			if (elementType != IElementType.WHITE_SPACE && elementType != Symbols.LINE_COMMENT && elementType != Symbols.BLOCK_COMMENT) {
				tokens.add(new CmTokenImpl(lexer.yyline + 1, lexer.yycolumn + 1, lexer.yytext().toString(), elementType));
			}
		}
		if (tokens.isEmpty()) {
			CompilationErrors.reportError(path, 0, 0, "empty source file");
			return null;
		}

		// run parser
		MapagGeneratedMahdlParser parser = new MapagGeneratedMahdlParser() {
			@Override
			protected void reportError(CmTokenImpl locationToken, String message) {
				int row = locationToken.getRow();
				int column = locationToken.getColumn();
				CompilationErrors.reportError(path, row, column, message);
			}
		};
		CmNode rootNode = parser.parse(tokens.toArray(new CmTokenImpl[0]));
		if (rootNode == null) {
			return null;
		}

		// check root node type
		if (rootNode instanceof Module) {
			return (Module) rootNode;
		} else {
			CompilationErrors.reportError(path, 0, 0, "wrong root CM node: " + rootNode);
			return null;
		}

	}

}
