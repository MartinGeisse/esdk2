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

    private final SimulatedRam.Implementation ram;
    private final RtlBitSignal dummyBit;
    private final RtlVectorSignal dummyVector24;
    private final RtlVectorSignal dummyVector32;

    public SimulatedRamAdapterWithoutRamdacSupport(RtlRealm realm, RtlClockNetwork clk) {
        super(realm);
        this.ram = new SimulatedRam.Implementation(realm, clk);
        this.dummyBit = new RtlBitConstant(realm, false);
        this.dummyVector24 = RtlVectorConstant.of(realm, 24, 0);
        this.dummyVector32 = RtlVectorConstant.of(realm, 32, 0);
    }

    public SimulatedRam.Implementation getRam() {
        return ram;
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
    public void setRamdacRequestEnable(RtlBitSignal ramdacRequestEnable) {
        // ignored
    }

    @Override
    public void setRamdacRequestWordAddress(RtlVectorSignal ramdacRequestWordAddress) {
        // ignored
    }

    @Override
    public RtlVectorSignal getRamdacResponseData() {
        return dummyVector32;
    }

    @Override
    public RtlBitSignal getRamdacResponseEnable() {
        return dummyBit;
    }

    @Override
    public RtlVectorSignal getRamdacResponseWordAddress() {
        return dummyVector24;
    }

}
