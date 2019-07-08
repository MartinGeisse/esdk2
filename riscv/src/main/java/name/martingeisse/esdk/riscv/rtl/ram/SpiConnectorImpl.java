package name.martingeisse.esdk.riscv.rtl.ram;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.riscv.rtl.spi.SpiConnector;

public class SpiConnectorImpl extends SpiConnector.Implementation {

    public SpiConnectorImpl(RtlRealm realm) {
        super(realm);
        // equivalent to SpiConnector.Implementation for now, but this will change when we need inputs to the FPGA
    }

}
