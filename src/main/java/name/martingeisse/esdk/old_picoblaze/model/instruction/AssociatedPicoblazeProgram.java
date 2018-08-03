package name.martingeisse.esdk.old_picoblaze.model.instruction;

import name.martingeisse.esdk.core.util.AssociatedResourceUtil;
import name.martingeisse.esdk.old_picoblaze.assembler.IPicoblazeAssemblerErrorHandler;
import name.martingeisse.esdk.old_picoblaze.assembler.Range;
import name.martingeisse.esdk.old_picoblaze.assembler.ast.AstBuilder;
import name.martingeisse.esdk.old_picoblaze.assembler.ast.Context;
import name.martingeisse.esdk.old_picoblaze.assembler.ast.PsmFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Loads and assembles a Picoblaze program from an associated resource with extension .psm; either from a subclass
 * of this class (the default if no class is specified) or an explicitly specified class.
 */
public class AssociatedPicoblazeProgram implements PicoblazeProgramHandler {

	private final int[] program;

	public AssociatedPicoblazeProgram() {
		this(null, null);
	}

	public AssociatedPicoblazeProgram(Class<?> anchorClass) {
		this(anchorClass, null);
	}

	public AssociatedPicoblazeProgram(String suffix) {
		this(null, suffix);
	}

	public AssociatedPicoblazeProgram(Class<?> anchorClass, String suffix) {
		IPicoblazeAssemblerErrorHandler errorHandler = new IPicoblazeAssemblerErrorHandler() {

			@Override
			public void handleWarning(final Range range, final String message) {
				handleError(range, message);
			}

			@Override
			public void handleError(final Range range, final String message) {
				throw new RuntimeException("ERROR in Picoblaze program for " + anchorClass + ", suffix " + suffix +
					" at " + range + ": " + message);
			}

		};
		try (InputStream inputStream = AssociatedResourceUtil.open(anchorClass, suffix, ".psm")) {
			try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1)) {
				final AstBuilder astBuilder = new AstBuilder();
				astBuilder.parse(reader, errorHandler);
				PsmFile psmFile = astBuilder.getResult();
				Context context = new Context(errorHandler);
				psmFile.collectConstantsAndLabels(context);
				program = psmFile.encode(context, errorHandler);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readInstruction(int address) {
		return program[address & 1023];
	}

}
