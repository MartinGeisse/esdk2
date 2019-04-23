package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlProceduralMemory extends RtlItem  {

	private final RtlClockedBlock block;
	private final Matrix matrix;

	public RtlProceduralMemory(RtlClockedBlock block, int rowCount, int columnCount) {
		this(block, new Matrix(rowCount, columnCount));
	}

	public RtlProceduralMemory(RtlClockedBlock block, Matrix matrix) {
		super(block.getRealm());
		this.block = block;
		this.matrix = matrix;
	}

	public RtlClockedBlock getBlock() {
		return block;
	}

	public Matrix getMatrix() {
		return matrix;
	}



	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the value from the stored next value.
	 */
	void updateMatrix() {
		// TODO
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		// procedural signals are synthesized as part of the block that defines them
		return new EmptyVerilogContribution();
	}










	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return value;
	}

	@Override
	public VectorValue getNextValue() {
		return nextValue;
	}

	@Override
	public void setNextValue(VectorValue nextValue) {
		if (nextValue == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (nextValue.getWidth() != width) {
			throw new IllegalArgumentException("trying to set next value of wrong width " + nextValue.getWidth() + ", should be " + width);
		}
		this.nextValue = nextValue;
	}

	@Override
	void updateValue() {
		value = nextValue;
	}

}
