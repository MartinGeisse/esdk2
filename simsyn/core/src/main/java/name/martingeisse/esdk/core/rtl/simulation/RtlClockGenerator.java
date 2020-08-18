package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;

/**
 *
 */
public final class RtlClockGenerator extends RtlSimulationItem {

	private final RtlClockNetwork clockNetwork;
	private final long period;
	private final long initialOffset;

	public RtlClockGenerator(RtlClockNetwork clockNetwork, long period) {
		this(clockNetwork, period, 0);
	}

	public RtlClockGenerator(RtlClockNetwork clockNetwork, long period, long initialOffset) {
		super(clockNetwork.getRealm());
		this.clockNetwork = clockNetwork;
		this.period = period;
		this.initialOffset = initialOffset;
	}

	public RtlClockNetwork getClockNetwork() {
		return clockNetwork;
	}

	public long getPeriod() {
		return period;
	}

	@Override
	protected void initializeSimulation() {
		fire(this::callback, initialOffset);
	}

	private void callback() {
		clockNetwork.simulateClockEdge();
		fire(this::callback, period);
	}

}
