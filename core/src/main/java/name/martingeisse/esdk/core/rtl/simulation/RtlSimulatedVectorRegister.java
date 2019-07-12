package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Vector version of {@link RtlSimulatedRegister}.
 */
public final class RtlSimulatedVectorRegister extends RtlSimulatedRegister implements RtlVectorSignal {

    private final int width;
    private VectorValue value;
    private VectorValue nextValue;

    public RtlSimulatedVectorRegister(RtlClockNetwork clock, int width) {
        super(clock);
        this.width = 0;
        this.value = VectorValue.of(width, 0);
    }

    public int getWidth() {
        return width;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // simulation
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    public VectorValue getValue() {
        return value;
    }

    public VectorValue getNextValue() {
        return nextValue;
    }

    public void setNextValue(VectorValue nextValue) {
        if (nextValue.getWidth() != width) {
            throw new IllegalArgumentException("got vector value of wrong width " + value.getWidth() + ", expected " + width);
        }
        this.nextValue = nextValue;
    }

    @Override
    public void computeNextState() {
        // must be manually set from the outside
    }

    @Override
    public void updateState() {
        this.value = nextValue;
    }

}
