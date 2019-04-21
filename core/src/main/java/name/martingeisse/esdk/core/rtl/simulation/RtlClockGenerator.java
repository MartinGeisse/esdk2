package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 *
 */
public final class RtlClockGenerator extends RtlItem {

	private final RtlClockNetwork clockNetwork;
	private final long period;

	public RtlClockGenerator(RtlClockNetwork clockNetwork, long period) {
		super(clockNetwork.getRealm());
		this.clockNetwork = clockNetwork;
		this.period = period;
	}

	public RtlClockNetwork getClockNetwork() {
		return clockNetwork;
	}

	public long getPeriod() {
		return period;
	}

	@Override
	protected void initializeSimulation() {
		fire(this::callback, 0);
	}

	private void callback() {
		clockNetwork.simulateClockEdge();
		fire(this::callback, period);
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
