package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlProceduralMemory extends RtlItem {

	private final RtlClockedBlock block;
	private final Matrix matrix;
	private final List<Update> updates = new ArrayList<>();

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
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public RtlVectorSignal select(RtlVectorSignal index) {
		return new RtlProceduralMemoryIndexSelection(getRealm(), this, index);
	}

	public RtlVectorSignal select(int index) {
		return new RtlProceduralMemoryConstantIndexSelection(getRealm(), this, index);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	public void requestUpdate(int index, VectorValue value) {
		if (index < 0 || index >= matrix.getRowCount()) {
			throw new IllegalArgumentException("invalid index: " + index);
		}
		if (value.getWidth() != matrix.getColumnCount()) {
			throw new IllegalArgumentException("new value has width " + value.getWidth() + ", expected " + matrix.getColumnCount());
		}
		Update update = new Update();
		update.index = index;
		update.value = value;
		updates.add(update);
	}

	/**
	 * Updates the value from the stored next value.
	 */
	void updateMatrix() {
		for (Update update : updates) {
			matrix.setRow(update.index, update.value);
		}
		updates.clear();
	}

	private static class Update {
		private int index;
		private VectorValue value;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		// procedural signals are synthesized as part of the block that defines them
		return new EmptyVerilogContribution();
	}

}
