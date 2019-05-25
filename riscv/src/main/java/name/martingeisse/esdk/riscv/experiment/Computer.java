package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConstantIndexSelection;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;

/**
 *
 */
public class Computer extends Design {

    private final RtlRealm realm;
    private final RtlClockNetwork clock;
    private final ComputerModule computerModule;

    public Computer() {
        this.realm = new RtlRealm(this);
        this.clock = realm.createClockNetwork(clockPin(realm));
        this.computerModule = new ComputerModule(realm, clock);

        vgaPin(realm, "H14", computerModule._textDisplay._vgaConnector.getR());
        vgaPin(realm, "H15", computerModule._textDisplay._vgaConnector.getG());
        vgaPin(realm, "G15", computerModule._textDisplay._vgaConnector.getB());
        vgaPin(realm, "F15", computerModule._textDisplay._vgaConnector.getHsync());
        vgaPin(realm, "F14", computerModule._textDisplay._vgaConnector.getVsync());

        computerModule._keyboard._ps2.setClk(ps2Pin(realm, "G14"));
        computerModule._keyboard._ps2.setData(ps2Pin(realm, "G13"));

		/*
	inout 	[15:0] sd_D_IO;
	inout	sd_UDQS_IO;
	inout	sd_LDQS_IO;
	// internal interface signals
	input 	clk0;
	input	clk90;
	input   clk180;
	input	clk270;
	input	reset;
		 */
		/*
        RtlModuleInstance ramController = new RtlModuleInstance(realm, "ddr_sdram");
        ramOutputPin(realm, "J5", ramController.createBitOutputPort("sd_CK_P"));
        ramOutputPin(realm, "J4", ramController.createBitOutputPort("sd_CK_N"));
        ramOutputPin(realm, "K4", ramController.createBitOutputPort("sd_CS_O"));
        ramOutputPin(realm, "K3", ramController.createBitOutputPort("sd_CKE_O"));
        ramOutputPin(realm, "C1", ramController.createBitOutputPort("sd_RAS_O"));
        ramOutputPin(realm, "C2", ramController.createBitOutputPort("sd_CAS_O"));
        ramOutputPin(realm, "D1", ramController.createBitOutputPort("sd_WE_O"));
        ramOutputPin(realm, "J1", ramController.createBitOutputPort("sd_UDM_O"));
        ramOutputPin(realm, "J2", ramController.createBitOutputPort("sd_LDM_O"));
        ramOutputPinArray(realm, ramController.createVectorOutputPort("sd_A_O", 13),
                "T1", "R3", "R2", "P1", "F4", "H4", "H3", "H1", "H2", "N4", "T2", "N5", "P2");
        ramOutputPinArray(realm, ramController.createVectorOutputPort("sd_BA_O", 2), "K5", "K6");
        ramController.createBitInputPort("wSTB_I", computerModule._bigRam.getEnable());
        ramController.createVectorInputPort("wADR_I", 24, computerModule._bigRam.getWordAddress());
        ramController.createBitInputPort("wWE_I", computerModule._bigRam.getWrite());
        ramController.createVectorInputPort("wDAT_I", 32, computerModule._bigRam.getWriteData());
        ramController.createVectorInputPort("wWRB_I", 4, computerModule._bigRam.getWriteMask());
        computerModule._bigRam.getReadData().setConnected(ramController.createVectorOutputPort("wDAT_O", 32));
        computerModule._bigRam.getAcknowledge().setConnected(ramController.createBitOutputPort("wACK_O"));
		 */

    }

    public RtlRealm getRealm() {
        return realm;
    }

    public RtlClockNetwork getClock() {
        return clock;
    }

    public ComputerModule getComputerModule() {
        return computerModule;
    }

    private static RtlInputPin clockPin(RtlRealm realm) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("LVCMOS33");
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId("C9");
        pin.setConfiguration(configuration);
        return pin;
    }

    private static RtlOutputPin vgaPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("LVTTL");
        configuration.setDrive(8);
        configuration.setSlew(XilinxPinConfiguration.Slew.FAST);
        RtlOutputPin pin = new RtlOutputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        pin.setOutputSignal(outputSignal);
        return pin;
    }

    private static RtlInputPin ps2Pin(RtlRealm realm, String id) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("LVCMOS33");
        configuration.setDrive(8);
        configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        return pin;
    }

    private static RtlInputPin ramInputPin(RtlRealm realm, String id) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("SSTL2_I");
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        return pin;
    }

    private static RtlOutputPin ramOutputPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("SSTL2_I");
        RtlOutputPin pin = new RtlOutputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        pin.setOutputSignal(outputSignal);
        return pin;
    }

    private static void ramOutputPinArray(RtlRealm realm, RtlVectorSignal outputSignal, String... ids) {
        if (ids.length != outputSignal.getWidth()) {
            throw new IllegalArgumentException("vector width (" + outputSignal.getWidth() +
                    ") does not match number of pin IDs (" + ids.length + ")");
        }
        for (int i = 0; i < outputSignal.getWidth(); i++) {
            RtlBitSignal bitSignal = new RtlConstantIndexSelection(realm, outputSignal, i);
            ramOutputPin(realm, ids[i], bitSignal);
        }
    }

}
