package name.martingeisse.esdk.riscv.rtl.ram;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.*;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConstantIndexSelection;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;

public class RamController extends RtlItem {

    public RamController(RtlRealm realm,
                         RtlBitSignal ddrClk0, RtlBitSignal ddrClk90, RtlBitSignal ddrClk180, RtlBitSignal ddrClk270,
                         RtlBitSignal reset,
                         RamControllerAdapter adapter) {
        super(realm);
        RtlModuleInstance controllerCore = new RtlModuleInstance(realm, "ddr_sdram");

        // generate DDR clock signal CK_P
        RtlModuleInstance sdramCkpDdr = new RtlModuleInstance(realm, "ODDR2");
        sdramCkpDdr.getParameters().put("DDR_ALIGNMENT", "NONE");
        sdramCkpDdr.getParameters().put("INIT", VectorValue.of(1, 0));
        sdramCkpDdr.getParameters().put("SRTYPE", "SYNC");
        sdramCkpDdr.setName("sdramCkpDdr");
        ramOutputPin(realm, "J5", sdramCkpDdr.createBitOutputPort("Q"));
        sdramCkpDdr.createBitInputPort("C0", ddrClk180);
        sdramCkpDdr.createBitInputPort("C1", ddrClk0);
        sdramCkpDdr.createBitInputPort("CE", new RtlBitConstant(realm, true));
        sdramCkpDdr.createBitInputPort("D0", new RtlBitConstant(realm, true));
        sdramCkpDdr.createBitInputPort("D1", new RtlBitConstant(realm, false));
        sdramCkpDdr.createBitInputPort("R", new RtlBitConstant(realm, false));
        sdramCkpDdr.createBitInputPort("S", new RtlBitConstant(realm, false));

        // generate DDR clock signal CK_N
        RtlModuleInstance sdramCknDdr = new RtlModuleInstance(realm, "ODDR2");
        sdramCknDdr.getParameters().put("DDR_ALIGNMENT", "NONE");
        sdramCknDdr.getParameters().put("INIT", VectorValue.of(1, 0));
        sdramCknDdr.getParameters().put("SRTYPE", "SYNC");
        sdramCknDdr.setName("sdramCknDdr");
        ramOutputPin(realm, "J4", sdramCknDdr.createBitOutputPort("Q"));
        sdramCknDdr.createBitInputPort("C0", ddrClk0);
        sdramCknDdr.createBitInputPort("C1", ddrClk180);
        sdramCknDdr.createBitInputPort("CE", new RtlBitConstant(realm, true));
        sdramCknDdr.createBitInputPort("D0", new RtlBitConstant(realm, true));
        sdramCknDdr.createBitInputPort("D1", new RtlBitConstant(realm, false));
        sdramCknDdr.createBitInputPort("R", new RtlBitConstant(realm, false));
        sdramCknDdr.createBitInputPort("S", new RtlBitConstant(realm, false));

        // system signals
        controllerCore.createBitInputPort("clk0", ddrClk0);
        controllerCore.createBitInputPort("clk90", ddrClk90);
        controllerCore.createBitInputPort("clk180", ddrClk180);
        controllerCore.createBitInputPort("clk270", ddrClk270);
        controllerCore.createBitInputPort("reset", reset);

        // SDRAM DDR interface
        ramOutputPin(realm, "K4", controllerCore.createBitOutputPort("sd_CS_O"));
        ramOutputPin(realm, "K3", controllerCore.createBitOutputPort("sd_CKE_O"));
        ramOutputPin(realm, "C1", controllerCore.createBitOutputPort("sd_RAS_O"));
        ramOutputPin(realm, "C2", controllerCore.createBitOutputPort("sd_CAS_O"));
        ramOutputPin(realm, "D1", controllerCore.createBitOutputPort("sd_WE_O"));
        ramOutputPin(realm, "J1", controllerCore.createBitOutputPort("sd_UDM_O"));
        ramOutputPin(realm, "J2", controllerCore.createBitOutputPort("sd_LDM_O"));
        ramOutputPinArray(realm, controllerCore.createVectorOutputPort("sd_A_O", 13),
                "T1", "R3", "R2", "P1", "F4", "H4", "H3", "H1", "H2", "N4", "T2", "N5", "P2");
        ramOutputPinArray(realm, controllerCore.createVectorOutputPort("sd_BA_O", 2), "K5", "K6");
        ramBidirectionalPin(realm, "G3", controllerCore, "sd_UDQS_IO");
        ramBidirectionalPin(realm, "L6", controllerCore, "sd_LDQS_IO");
        RtlBidirectionalModulePortPinArray ramDataPinArray = new RtlBidirectionalModulePortPinArray(
                realm, controllerCore, "sd_D_IO", "sd_D_IO",
                "L2", "L1", "L3", "L4", "M3", "M4", "M5", "M6", "E2", "E1", "F1", "F2", "G6", "G5", "H6", "H5"
        );
        for (RtlPin pin : ramDataPinArray.getPins()) {
            XilinxPinConfiguration configuration = new XilinxPinConfiguration();
            configuration.setIostandard("SSTL2_I");
            pin.setConfiguration(configuration);
        }

        // internal Wishbone interface
        controllerCore.createBitInputPort("wSTB_I", adapter.getEnable());
        controllerCore.createVectorInputPort("wADR_I", 24, adapter.getWordAddress());
        controllerCore.createBitInputPort("wWE_I", adapter.getWrite());
        controllerCore.createVectorInputPort("wDAT_I", 32, adapter.getWriteData());
        controllerCore.createVectorInputPort("wWRB_I", 4, adapter.getWriteMask());
        adapter.getReadData().setConnected(controllerCore.createVectorOutputPort("wDAT_O", 32));
        adapter.getAcknowledge().setConnected(controllerCore.createBitOutputPort("wACK_O"));

    }

    @Override
    public VerilogContribution getVerilogContribution() {
        return new EmptyVerilogContribution();
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

    private static RtlBidirectionalModulePortPin ramBidirectionalPin(RtlRealm realm, String pinId, RtlModuleInstance moduleInstance, String portName) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("SSTL2_I");
        RtlBidirectionalModulePortPin pin = new RtlBidirectionalModulePortPin(realm, moduleInstance, portName);
        pin.setId(pinId);
        pin.setConfiguration(configuration);
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
