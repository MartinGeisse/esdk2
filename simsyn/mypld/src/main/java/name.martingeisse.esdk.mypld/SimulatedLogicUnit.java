package name.martingeisse.esdk.mypld;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedComputedBitSignal;

public class SimulatedLogicUnit extends RtlClockedSimulationItem {

    private int lutContents;
    private boolean clocked;
    private RtlBitSignal d0, d1, d2, d3;
    private boolean registeredValue, nextRegisteredValue;
    private final RtlBitSignal q;

    public SimulatedLogicUnit(RtlClockNetwork clockNetwork) {
        super(clockNetwork);
        q = new RtlSimulatedComputedBitSignal(clockNetwork.getRealm()) {
            @Override
            public boolean getValue() {
                return clocked ? registeredValue : computeLutFunction();
            }
        };
    }

    // 1 input signal, 2^1 = 2 possible input values, 2^2 = 4 possible LUT contents
    public static RtlBitSignal buildUnclocked(RtlRealm realm, int lutContents, RtlBitSignal d0) {
        checkLutContents(lutContents, 1);
        RtlBitConstant falseConstant = new RtlBitConstant(realm, false);
        return buildUnclocked(realm, lutContents, d0, falseConstant, falseConstant, falseConstant);
    }

    // 2 input signals, 2^2=4 possible input values, 2^4 = 16 possible LUT contents
    public static RtlBitSignal buildUnclocked(RtlRealm realm, int lutContents, RtlBitSignal d0, RtlBitSignal d1) {
        checkLutContents(lutContents, 2);
        RtlBitConstant falseConstant = new RtlBitConstant(realm, false);
        return buildUnclocked(realm, lutContents, d0, d1, falseConstant, falseConstant);
    }

    // 3 input signals, 2^3=8 possible input values, 2^8 = 256 possible LUT contents
    public static RtlBitSignal buildUnclocked(RtlRealm realm, int lutContents, RtlBitSignal d0,
                                              RtlBitSignal d1, RtlBitSignal d2) {
        checkLutContents(lutContents, 3);
        RtlBitConstant falseConstant = new RtlBitConstant(realm, false);
        return buildUnclocked(realm, lutContents, d0, d1, d2, falseConstant);
    }

    // 4 input signals, 2^4=16 possible input values, 2^16 possible LUT contents
    public static RtlBitSignal buildUnclocked(RtlRealm realm, int lutContents, RtlBitSignal d0,
                                              RtlBitSignal d1, RtlBitSignal d2, RtlBitSignal d3) {
        SimulatedLogicUnit logicUnit = new SimulatedLogicUnit(realm.getNullClockNetwork());
        logicUnit.setLutContents(lutContents);
        logicUnit.setClocked(false);
        logicUnit.setD0(d0);
        logicUnit.setD1(d1);
        logicUnit.setD2(d2);
        logicUnit.setD3(d3);
        return logicUnit.getQ();
    }

    // 1 input signal, 2^1 = 2 possible input values, 2^2 = 4 possible LUT contents
    public static RtlBitSignal buildClocked(RtlClockNetwork clock, int lutContents, RtlBitSignal d0) {
        checkLutContents(lutContents, 1);
        RtlBitConstant falseConstant = new RtlBitConstant(clock.getRealm(), false);
        return buildClocked(clock, lutContents, d0, falseConstant, falseConstant, falseConstant);
    }

    // 2 input signals, 2^2=4 possible input values, 2^4 = 16 possible LUT contents
    public static RtlBitSignal buildClocked(RtlClockNetwork clock, int lutContents, RtlBitSignal d0, RtlBitSignal d1) {
        checkLutContents(lutContents, 2);
        RtlBitConstant falseConstant = new RtlBitConstant(clock.getRealm(), false);
        return buildClocked(clock, lutContents, d0, d1, falseConstant, falseConstant);
    }

    // 3 input signals, 2^3=8 possible input values, 2^8 = 256 possible LUT contents
    public static RtlBitSignal buildClocked(RtlClockNetwork clock, int lutContents, RtlBitSignal d0,
                                            RtlBitSignal d1, RtlBitSignal d2) {
        checkLutContents(lutContents, 3);
        RtlBitConstant falseConstant = new RtlBitConstant(clock.getRealm(), false);
        return buildClocked(clock, lutContents, d0, d1, d2, falseConstant);
    }

    // 4 input signals, 2^4=16 possible input values, 2^16 possible LUT contents
    public static RtlBitSignal buildClocked(RtlClockNetwork clock, int lutContents, RtlBitSignal d0,
                                            RtlBitSignal d1, RtlBitSignal d2, RtlBitSignal d3) {
        SimulatedLogicUnit logicUnit = new SimulatedLogicUnit(clock);
        logicUnit.setLutContents(lutContents);
        logicUnit.setClocked(true);
        logicUnit.setD0(d0);
        logicUnit.setD1(d1);
        logicUnit.setD2(d2);
        logicUnit.setD3(d3);
        return logicUnit.getQ();
    }

    public int getLutContents() {
        return lutContents;
    }

    public void setLutContents(int lutContents) {
        checkLutContents(lutContents, 4);
        this.lutContents = lutContents;
    }

    public boolean isClocked() {
        return clocked;
    }

    public void setClocked(boolean clocked) {
        this.clocked = clocked;
    }

    public RtlBitSignal getD0() {
        return d0;
    }

    public void setD0(RtlBitSignal d0) {
        this.d0 = d0;
    }

    public RtlBitSignal getD1() {
        return d1;
    }

    public void setD1(RtlBitSignal d1) {
        this.d1 = d1;
    }

    public RtlBitSignal getD2() {
        return d2;
    }

    public void setD2(RtlBitSignal d2) {
        this.d2 = d2;
    }

    public RtlBitSignal getD3() {
        return d3;
    }

    public void setD3(RtlBitSignal d3) {
        this.d3 = d3;
    }

    public RtlBitSignal getQ() {
        return q;
    }

    private boolean computeLutFunction() {
        int address = (d0.getValue() ? 1 : 0) + (d1.getValue() ? 2 : 0) + (d2.getValue() ? 4 : 0) + (d3.getValue() ? 8 : 0);
        return (lutContents & (1 << address)) != 0;
    }

    @Override
    public void computeNextState() {
        nextRegisteredValue = computeLutFunction();
    }

    @Override
    public void updateState() {
        registeredValue = nextRegisteredValue;
    }

    private static void checkLutContents(int lutContents, int inputSignals) {
        int possibleInputValues = (1 << inputSignals);
        int possibleLutContents = (1 << possibleInputValues);
        if (lutContents != (lutContents & (possibleLutContents - 1))) {
            throw new IllegalArgumentException("invalid LUT contents for " + inputSignals + " input signals: " + lutContents);
        }
    }

}
