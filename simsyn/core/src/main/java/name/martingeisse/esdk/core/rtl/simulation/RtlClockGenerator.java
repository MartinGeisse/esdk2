package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;

/**
 *
 */
public final class RtlClockGenerator extends RtlIntervalSimulationItem {

    public RtlClockGenerator(RtlClockNetwork clockNetwork, long period) {
        super(clockNetwork.getRealm(), period, clockNetwork::simulateClockEdge);
    }

    public RtlClockGenerator(RtlClockNetwork clockNetwork, long period, long initialOffset) {
        super(clockNetwork.getRealm(), period, initialOffset, clockNetwork::simulateClockEdge);
    }

}
