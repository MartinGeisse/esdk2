package name.martingeisse.esdk.riscv.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;

public class SimulationDeviceImpl extends RtlClockedSimulationItem implements SimulationDevice {

    private final SimulationDeviceDelegate delegate;

    private final RtlBitSignalConnector busEnable;
    private final RtlVectorSignalConnector busWordAddress;
    private final RtlBitSignalConnector busWrite;
    private final RtlVectorSignalConnector busWriteData;
    private final RtlVectorSignalConnector busWriteMask;

    private final RtlSimulatedVectorRegister readData;
    private final RtlSimulatedBitRegister secondCycle;

    public SimulationDeviceImpl(RtlRealm realm, RtlClockNetwork clock, SimulationDeviceDelegate delegate) {
        super(clock);
        this.delegate = delegate;

        this.busEnable = new RtlBitSignalConnector(realm);
        this.busEnable.setHierarchyParent(this);
        this.busEnable.setName("busEnable");
        this.busWordAddress = new RtlVectorSignalConnector(realm, 24);
        this.busWordAddress.setHierarchyParent(this);
        this.busWordAddress.setName("busWordAddress");
        this.busWrite = new RtlBitSignalConnector(realm);
        this.busWrite.setHierarchyParent(this);
        this.busWrite.setName("busWrite");
        this.busWriteData = new RtlVectorSignalConnector(realm, 32);
        this.busWriteData.setHierarchyParent(this);
        this.busWriteData.setName("busWriteData");
        this.busWriteMask = new RtlVectorSignalConnector(realm, 4);
        this.busWriteMask.setHierarchyParent(this);
        this.busWriteMask.setName("busWriteMask");

        this.readData = new RtlSimulatedVectorRegister(clock, 32);
        this.readData.setHierarchyParent(this);
        this.readData.setName("readData");
        this.secondCycle = new RtlSimulatedBitRegister(clock);
        this.secondCycle.setHierarchyParent(this);
        this.secondCycle.setName("secondCycle");
    }

    @Override
    public RtlBitSignal getBusAcknowledge() {
        return busWrite.or(secondCycle);
    }

    public void setBusEnable(RtlBitSignal busEnable) {
        this.busEnable.setConnected(busEnable);
    }

    @Override
    public RtlVectorSignal getBusReadData() {
        return readData;
    }

    public void setBusWordAddress(RtlVectorSignal busWordAddress) {
        this.busWordAddress.setConnected(busWordAddress);
    }

    public void setBusWrite(RtlBitSignal busWrite) {
        this.busWrite.setConnected(busWrite);
    }

    public void setBusWriteData(RtlVectorSignal busWriteData) {
        this.busWriteData.setConnected(busWriteData);
    }

    public void setBusWriteMask(RtlVectorSignal busWriteMask) {
        this.busWriteMask.setConnected(busWriteMask);
    }

    @Override
    public void computeNextState() {
        secondCycle.setNextValue(false);
        if (busEnable.getValue()) {
            if (busWrite.getValue()) {
                delegate.write(busWordAddress.getValue().getAsUnsignedInt(), busWriteMask.getValue().getAsUnsignedInt());
            } else {
                if (!secondCycle.getValue()) {
                    readData.setNextValue(VectorValue.of(32, delegate.read(busWordAddress.getValue().getAsUnsignedInt())));
                    secondCycle.setNextValue(true);
                }
            }
        }
    }

    @Override
    public void updateState() {
    }

}
