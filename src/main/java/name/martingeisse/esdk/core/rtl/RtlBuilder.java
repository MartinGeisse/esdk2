package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitSignal;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlBuilder {

	// prevent instantiation
	private RtlBuilder() {
	}

	public static RtlBitSignal bitRegister(RtlClockNetwork clock, RtlBitSignal data) {
		return bitRegister(clock, data, null, false, false);
	}

	public static RtlBitSignal bitRegister(RtlClockNetwork clock, RtlBitSignal data, boolean initialValue) {
		return bitRegister(clock, data, null, true, initialValue);
	}

	public static RtlBitSignal bitRegister(RtlClockNetwork clock, RtlBitSignal data, RtlBitSignal enable) {
		return bitRegister(clock, data, enable, false, false);
	}

	public static RtlBitSignal bitRegister(RtlClockNetwork clock, RtlBitSignal data, RtlBitSignal enable, boolean initialValue) {
		return bitRegister(clock, data, enable, true, initialValue);
	}

	private static RtlBitSignal bitRegister(RtlClockNetwork clock, RtlBitSignal data, RtlBitSignal enable, boolean initialize, boolean initialValue) {
		RtlClockedBlock block = new RtlClockedBlock(clock);
		RtlProceduralBitSignal register = block.createBit();
		if (initialize) {
			block.getInitializerStatements().assign(register, initialValue);
		}
		RtlStatementSequence sequence = enable == null ? block.getStatements() : block.getStatements().when(enable).getThenBranch();
		sequence.assign(register, data);
		return register;
	}

	public static RtlVectorSignal vectorRegister(RtlClockNetwork clock, RtlVectorSignal data) {
		return vectorRegister(clock, data, null, null);
	}

	public static RtlVectorSignal vectorRegister(RtlClockNetwork clock, RtlVectorSignal data, VectorValue initialValue) {
		return vectorRegister(clock, data, null, initialValue);
	}

	public static RtlVectorSignal vectorRegister(RtlClockNetwork clock, RtlVectorSignal data, RtlBitSignal enable) {
		return vectorRegister(clock, data, enable, null);
	}

	public static RtlVectorSignal vectorRegister(RtlClockNetwork clock, RtlVectorSignal data, RtlBitSignal enable, VectorValue initialValue) {
		RtlClockedBlock block = new RtlClockedBlock(clock);
		RtlProceduralVectorSignal register = block.createVector(data.getWidth());
		if (initialValue != null) {
			block.getInitializerStatements().assign(register, initialValue);
		}
		RtlStatementSequence sequence = enable == null ? block.getStatements() : block.getStatements().when(enable).getThenBranch();
		sequence.assign(register, data);
		return register;
	}

}
