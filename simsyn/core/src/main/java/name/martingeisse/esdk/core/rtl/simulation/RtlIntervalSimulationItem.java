package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;

/**
 * Executes some code periodically in simulated time.
 */
public class RtlIntervalSimulationItem extends RtlSimulationItem {

    private final long period;
    private final long initialOffset;
    private final Runnable action;

    public RtlIntervalSimulationItem(RtlRealm realm, long period, Runnable action) {
        this(realm, period, 0, action);
    }

    public RtlIntervalSimulationItem(RtlRealm realm, long period, long initialOffset, Runnable action) {
        super(realm);
        this.period = period;
        this.initialOffset = initialOffset;
        this.action = action;
    }

    public long getPeriod() {
        return period;
    }

    public long getInitialOffset() {
        return initialOffset;
    }

    public Runnable getAction() {
        return action;
    }

    @Override
    protected void initializeSimulation() {
        fire(this::callback, initialOffset);
    }

    private void callback() {
        action.run();
        fire(this::callback, period);
    }

}
