package name.martingeisse.esdk.riscv.rtl.ram;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;

/**
 * Wraps {@link SimulatedRam} to implement {@link RamController}.
 * <p>
 * The RAMDAC ports of the {@link RamController} are not supported and are ignored / tied to constant values.
 * The reason for this is that the {@link SimulatedRam} does not support multiple clients either.
 */
public class SimulatedRamAdapterWithoutRamdacSupport extends RtlSimulationItem implements RamController {

    private final SimulatedRam ram;
    private final RtlBitSignal dummyBit;
    private final RtlVectorSignal dummyVector32;

    public SimulatedRamAdapterWithoutRamdacSupport(RtlRealm realm, RtlClockNetwork clk) {
        super(realm);
        this.ram = new SimulatedRam.Implementation(realm, clk);
        this.dummyBit = new RtlBitConstant(realm, false);
        this.dummyVector32 = RtlVectorConstant.of(realm, 32, 0);
    }

    //
    // general signals
    //

    @Override
    public void setReset(RtlBitSignal reset) {
        // ignored
    }

    //
    // bus signals
    //

    @Override
    public RtlBitSignal getBusAcknowledge() {
        return ram.getAcknowledge();
    }

    @Override
    public void setBusEnable(RtlBitSignal busEnable) {
        ram.setEnable(busEnable);
    }

    @Override
    public RtlVectorSignal getBusReadData() {
        return ram.getReadData();
    }

    @Override
    public void setBusWordAddress(RtlVectorSignal busWordAddress) {
        ram.setWordAddress(busWordAddress);
    }

    @Override
    public void setBusWrite(RtlBitSignal busWrite) {
        ram.setWrite(busWrite);
    }

    @Override
    public void setBusWriteData(RtlVectorSignal busWriteData) {
        ram.setWriteData(busWriteData);
    }

    @Override
    public void setBusWriteMask(RtlVectorSignal busWriteMask) {
        ram.setWriteMask(busWriteMask);
    }

    //
    // RAMDAC signals
    //

    @Override
    public RtlBitSignal getRamdacAcknowledge() {
        return dummyBit;
    }

    @Override
    public void setRamdacEnable(RtlBitSignal ramdacEnable) {
        // ignored
    }

    @Override
    public RtlVectorSignal getRamdacReadData() {
        return dummyVector32;
    }

    @Override
    public void setRamdacWordAddress(RtlVectorSignal ramdacWordAddress) {
        // ignored
    }

}
