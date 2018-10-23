package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Same as {@link RtlBitSampler} but for vector signals. See that class for an explanation.
 */
public final class RtlVectorSampler extends RtlClockedItem {

	private final RtlVectorSignal signal;
	private VectorValue sample;

	public RtlVectorSampler(RtlClockNetwork clockNetwork, RtlVectorSignal signal) {
		super(clockNetwork);
		this.signal = signal;
	}

	public int getWidth() {
		return signal.getWidth();
	}

	public VectorValue getSample() {
		return sample;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		this.sample = VectorValue.ofUnsigned(getWidth(), 0);
	}

	@Override
	public void computeNextState() {
		sample = signal.getValue();
	}

	@Override
	public void updateState() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
