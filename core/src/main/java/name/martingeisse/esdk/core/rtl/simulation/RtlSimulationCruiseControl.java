package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;

/**
 *
 */
public class RtlSimulationCruiseControl extends RtlSimulationItem {

	private final long simulationPeriodTicks;
	private final long realTimePeriodMilliseconds;
	private long lastTime;

	public RtlSimulationCruiseControl(RtlRealm realm, long simulationPeriodTicks, long realTimePeriodMilliseconds) {
		super(realm);
		this.simulationPeriodTicks = simulationPeriodTicks;
		this.realTimePeriodMilliseconds = realTimePeriodMilliseconds;
	}

	@Override
	protected void initializeSimulation() {
		lastTime = System.currentTimeMillis();
		fire(this::callback, 0);
	}

	private void callback() {
		long now = System.currentTimeMillis();
		long elapsedDelta = now - lastTime;
		long pendingDelta = realTimePeriodMilliseconds - elapsedDelta;
		if (pendingDelta > 0) {
			try {
				Thread.sleep(pendingDelta);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		lastTime = now;
		fire(this::callback, simulationPeriodTicks);
	}

}
