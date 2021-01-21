package name.martingeisse.esdk.riscv.orange_crab;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.pin.simulation.RtlVectorInputPin;
import name.martingeisse.esdk.core.rtl.pin.simulation.RtlVectorOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogOutputGenerator;
import name.martingeisse.esdk.riscv.orange_crab.ddr3.RamController;
import name.martingeisse.esdk.riscv.orange_crab.ddr3.SdramConnector;

import java.io.File;

/**
 *
 */
public class SdramControllerGeneratorMain {

    public static void main(String[] args) throws Exception {

        // create design and realm
        Design design = new Design();
        RtlRealm realm = new RtlRealm(design);
        RtlClockNetwork clock = realm.createClockNetwork(bitInputPort(realm, "clk"));

        // create main RTL
        SdramConnector.Connector sdramConnector = new SdramConnector.Connector(realm);
        RamController ramController = new RamController.Implementation(realm, clock) {
            @Override
            protected SdramConnector createSdram(RtlRealm realm) {
                return sdramConnector;
            }
        };

        // control ports
        bitOutputPort(realm, "sdramCK", sdramConnector.getCKSocket());
        bitOutputPort(realm, "sdramCKE", sdramConnector.getCKESocket());
        bitOutputPort(realm, "sdramODT", sdramConnector.getODTSocket());
        bitOutputPort(realm, "sdramRESETn", sdramConnector.getRESETnSocket());
        bitOutputPort(realm, "sdramRASn", sdramConnector.getRASnSocket());
        bitOutputPort(realm, "sdramCASn", sdramConnector.getCASnSocket());
        bitOutputPort(realm, "sdramWEn", sdramConnector.getWEnSocket());
        bitOutputPort(realm, "sdramCSn", sdramConnector.getCSnSocket());
        vectorOutputPort(realm, "sdramA", sdramConnector.getASocket());
        vectorOutputPort(realm, "sdramBA", sdramConnector.getBASocket());

        // data ports
        bitOutputPort(realm, "sdramLDM", sdramConnector.getDataOutMaskSocket().select(0));
        bitOutputPort(realm, "sdramUDM", sdramConnector.getDataOutMaskSocket().select(1));
        bitOutputPort(realm, "sdramDriveData", sdramConnector.getDriveDataSocket());
        vectorOutputPort(realm, "sdramDataOut", sdramConnector.getDataOutSocket());
        sdramConnector.setDataInSocket(vectorInputPort(realm, "sdramDataIn", 16));
        bitOutputPort(realm, "sdramDriveDQS", sdramConnector.getDriveDataStrobeSocket());
        bitOutputPort(realm, "sdramDQS", sdramConnector.getDataStrobeOutSocket());

        // bus interface
        ramController.setReset(bitInputPort(realm, "reset"));
        ramController.setBusRequestEnable(bitInputPort(realm, "busEnable"));
        ramController.setBusRequestWordAddress(vectorInputPort(realm, "busRequestWordAddress", 25));
        ramController.setBusRequestWrite(bitInputPort(realm, "busRequestWrite"));
        ramController.setBusRequestWriteData(vectorInputPort(realm, "busRequestWriteData", 32));
        ramController.setBusRequestWriteMask(vectorInputPort(realm, "busRequestWriteMask", 4));
        bitOutputPort(realm, "busRequestAcknowledge", ramController.getBusRequestAcknowledge());
        vectorOutputPort(realm, "busResponseReadData", ramController.getBusResponseReadData());

        // generate the output module
        VerilogOutputGenerator verilogOutputGenerator = new VerilogOutputGenerator(realm, "SdramController", new File("synthesize/sdram-controller"));
        verilogOutputGenerator.clean();
        verilogOutputGenerator.generate();

    }

    private static void bitOutputPort(RtlRealm realm, String name, RtlBitSignal signal) {
        RtlOutputPin pin = new RtlOutputPin(realm);
        pin.setId("_" + name);
        pin.setOutputSignal(signal);
    }

    private static void vectorOutputPort(RtlRealm realm, String name, RtlVectorSignal signal) {
        RtlVectorOutputPin pin = new RtlVectorOutputPin(realm, signal.getWidth());
        pin.setId("_" + name);
        pin.setOutputSignal(signal);
    }

    private static RtlInputPin bitInputPort(RtlRealm realm, String name) {
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId("_" + name);
        return pin;
    }

    private static RtlVectorInputPin vectorInputPort(RtlRealm realm, String name, int width) {
        RtlVectorInputPin pin = new RtlVectorInputPin(realm, width);
        pin.setId("_" + name);
        return pin;
    }

}
