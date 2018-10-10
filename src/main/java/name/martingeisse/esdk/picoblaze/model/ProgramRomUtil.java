package name.martingeisse.esdk.picoblaze.model;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.picoblaze.model.instruction.AssociatedPicoblazeProgram;

/**
 *
 */
public final class ProgramRomUtil {

	// prevent instantiation
	private ProgramRomUtil() {
	}

	public static RtlMemory loadAssociatedProgramRom(RtlRealm realm, Class<?> anchorClass, String programSuffix) {
		RtlMemory rom = new RtlMemory(realm, 1024, 18);
		loadAssociatedProgramRom(rom.getMatrix(), anchorClass, programSuffix);
		return rom;
	}

	public static void loadAssociatedProgramRom(Matrix matrix, Class<?> anchorClass, String programSuffix) {
		AssociatedPicoblazeProgram program = new AssociatedPicoblazeProgram(anchorClass, programSuffix);
		for (int i = 0; i < 1024; i++) {
			matrix.setRow(i, VectorValue.ofUnsigned(18, program.readInstruction(i)));
		}
	}

}
