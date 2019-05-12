package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 * Samples a bit signal at clock edges and makes it available via a getter method. This is useful to implement custom
 * behavioral clocked items -- these have to sample their inputs during computeNextState() and change their outputs
 * during updateState(). Without this sampler class, each item has to implement the sampling logic manually in
 * computeNextState(), using fields to store the sampled values.
 *
 * During synthesis, this item vanishes to support building helper items that do not prevent synthesis. Usually though,
 * this item is used in a behavioral-only item which *does* prevent synthesis.
 */
public final class RtlBitSampler extends RtlClockedItem {

	private final RtlBitSignal signal;
	private boolean sample;

	public RtlBitSampler(RtlClockNetwork clockNetwork, RtlBitSignal signal) {
		super(clockNetwork);
		this.signal = signal;
	}

	public boolean getSample() {
		return sample;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
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
