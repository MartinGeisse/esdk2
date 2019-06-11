package name.martingeisse.esdk.riscv.rtl.ram;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.*;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
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

        //
        // clock signals
        //

        // generate DDR clock signal CK_P
        RtlModuleInstance sdramCkpDdr = new RtlModuleInstance(realm, "ODDR2");
        sdramCkpDdr.getParameters().put("DDR_ALIGNMENT", "NONE");
        sdramCkpDdr.getParameters().put("INIT", VectorValue.of(1, 0));
        sdramCkpDdr.getParameters().put("SRTYPE", "SYNC");
        sdramCkpDdr.setName("sdramCkpDdr");
        ramOutputPin(realm, "J5", sdramCkpDdr.createBitOutputPort("Q"));
        sdramCkpDdr.createBitInputPort("C0", ddrClk180);
        sdramCkpDdr.createBitInputPort("C1", ddrClk0);
        sdramCkpDdr.createBitInputPort("CE", true);
        sdramCkpDdr.createBitInputPort("D0", true);
        sdramCkpDdr.createBitInputPort("D1", false);
        sdramCkpDdr.createBitInputPort("R", false);
        sdramCkpDdr.createBitInputPort("S", false);

        // generate DDR clock signal CK_N
        RtlModuleInstance sdramCknDdr = new RtlModuleInstance(realm, "ODDR2");
        sdramCknDdr.getParameters().put("DDR_ALIGNMENT", "NONE");
        sdramCknDdr.getParameters().put("INIT", VectorValue.of(1, 0));
        sdramCknDdr.getParameters().put("SRTYPE", "SYNC");
        sdramCknDdr.setName("sdramCknDdr");
        ramOutputPin(realm, "J4", sdramCknDdr.createBitOutputPort("Q"));
        sdramCknDdr.createBitInputPort("C0", ddrClk0);
        sdramCknDdr.createBitInputPort("C1", ddrClk180);
        sdramCknDdr.createBitInputPort("CE", true);
        sdramCknDdr.createBitInputPort("D0", true);
        sdramCknDdr.createBitInputPort("D1", false);
        sdramCknDdr.createBitInputPort("R", false);
        sdramCknDdr.createBitInputPort("S", false);

        //
        // data I/O pins, IOBUFs, IDDRs/ODDRs
        //

        // pins
        RtlGenericPinArray ramDataPinArray = new RtlGenericPinArray(
                realm, "inout", "sd_D_IO",
                "L2", "L1", "L3", "L4", "M3", "M4", "M5", "M6", "E2", "E1", "F1", "F2", "G6", "G5", "H6", "H5"
        );
        for (RtlPin pin : ramDataPinArray.getPins()) {
            XilinxPinConfiguration configuration = new XilinxPinConfiguration();
            configuration.setIostandard("SSTL2_I");
            pin.setConfiguration(configuration);
        }

        // the external interface has 16 data bits
        RtlVectorSignal lowerDataBits = RtlVectorConstant.of(realm, 0, 0);
        RtlVectorSignal upperDataBits = RtlVectorConstant.of(realm, 0, 0);
        RtlBitSignal ddrInterfaceDataOutThreestate = controllerCore.createBitOutputPort("ddrInterfaceDataOutEnable").not();
        RtlVectorSignal ddrInterfaceDataOut = controllerCore.createVectorOutputPort("ddrInterfaceDataOut", 32);
        for (int i = 0; i < 16; i++) {
            int finalI = i;

            RtlBitSignalConnector inputDdrBitConnector = new RtlBitSignalConnector(realm);

            // TODO sampling at clk0 and clk180 seems wrong to me. The original ddr_sdram code said in a comment that
            // with CL=2 the data could for some strange reason be sampled at 2.5 cycles after issuing a READ
            // command, but it does not say whether the author expected 2 cycles or 2.25 cycles to be correct.
            // The DDR spec would demand 2.25 cycles -- 2 cycles after the READ comes the positive DQS edge, 0.25
            // cycles after that is the ideal time to sample read data. It would be relevant whether it was 2.0 or
            // 2.25 that did not work for the original module; 2.0 would indicate a misunderstanding about
            // DDR timing, while 2.25 would indicate a problem with how the SDRAM chip works or is connected.
            //
            // Note that the timing diagrams make DQS *look* like a clock-enable, but it is actually a clock signal
            // that must be phase-shifted for reading.
            RtlModuleInstance iddr = new RtlModuleInstance(realm, "IDDR2");
            iddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            iddr.getParameters().put("INIT_Q0", VectorValue.of(1, 0));
            iddr.getParameters().put("INIT_Q1", VectorValue.of(1, 0));
            iddr.getParameters().put("SRTYPE", "SYNC");
            iddr.createBitInputPort("C0", ddrClk180);
            iddr.createBitInputPort("C1", ddrClk0);
            iddr.createBitInputPort("CE", true);
            iddr.createBitInputPort("R", false);
            iddr.createBitInputPort("S", false);
            iddr.createBitInputPort("D", inputDdrBitConnector);
            lowerDataBits = iddr.createBitOutputPort("Q0").concat(lowerDataBits);
            upperDataBits = iddr.createBitOutputPort("Q1").concat(upperDataBits);

            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", ddrClk90);
            oddr.createBitInputPort("C1", ddrClk270);
            oddr.createBitInputPort("CE", true);
            oddr.createBitInputPort("R", false);
            oddr.createBitInputPort("S", false);
            oddr.createBitInputPort("D0", ddrInterfaceDataOut.select(i));
            oddr.createBitInputPort("D1", ddrInterfaceDataOut.select(i + 16));
            RtlBitSignal outputDdrBit = oddr.createBitOutputPort("Q");

            RtlModuleInstance iobuf = new RtlModuleInstance(realm, "IOBUF");
            iobuf.getParameters().put("DRIVE", 4);
            iobuf.getParameters().put("IBUF_DELAY_VALUE", "0");
            iobuf.getParameters().put("IFD_DELAY_VALUE", "AUTO");
            iobuf.getParameters().put("IOSTANDARD", "DEFAULT");
            iobuf.getParameters().put("SLEW", "SLOW");
            inputDdrBitConnector.setConnected(iobuf.createBitOutputPort("O"));
            new RtlInstancePort(iobuf, "IO") {
                @Override
                protected void printPortAssignment(VerilogWriter out) {
                    out.print("." + getPortName() + "(" + ramDataPinArray.getNetName() + '[' + finalI + "])");
                }
            };
            iobuf.createBitInputPort("I", outputDdrBit);
            iobuf.createBitInputPort("T", ddrInterfaceDataOutThreestate);

        }
        controllerCore.createVectorInputPort("ddrInterfaceDataIn", 32, upperDataBits.concat(lowerDataBits));

        //
        // write data masks (follow the same timing as write data) and corresponding IOBUFs and ODDRs
        //

        RtlVectorSignal ddrInterfaceDataOutMask = controllerCore.createVectorOutputPort("ddrInterfaceDataOutMask", 4);

        // TODO
        /*
	// Mask output

	wire		UDM_conn;
	wire		LDM_conn;

        ODDR2 #(
                .DDR_ALIGNMENT("NONE"),
                .INIT(1'b0),
                .SRTYPE("SYNC")
        ) ODDR2_inst_UDM (
                .Q(UDM_conn),
                .C0(clk90),
                .C1(clk270),
                .CE(1'b1),
                .D0(~D_mask_reg[1]),
                .D1(~D_mask_reg[3]),
                .R(1'b0),
                .S(1'b0)
        );

        ODDR2 #(
                .DDR_ALIGNMENT("NONE"),
                .INIT(1'b0),
                .SRTYPE("SYNC")
        ) ODDR2_inst_LDM (
                .Q(LDM_conn),
                .C0(clk90),
                .C1(clk270),
                .CE(1'b1),
                .D0(~D_mask_reg[0]),
                .D1(~D_mask_reg[2]),
                .R(1'b0),
                .S(1'b0)
        );

        IOBUF #(
                .DRIVE(4),
                .IBUF_DELAY_VALUE("0"),
                .IFD_DELAY_VALUE("AUTO"),
                .IOSTANDARD("DEFAULT"),
                .SLEW("SLOW")
        ) IOBUF_inst_UDM (
                // .O() intentionally not connected
                .IO(sd_UDM_O),
                .I(UDM_conn),
                .T(1'b0)
        );

        IOBUF #(
                .DRIVE(4),
                .IBUF_DELAY_VALUE("0"),
                .IFD_DELAY_VALUE("AUTO"),
                .IOSTANDARD("DEFAULT"),
                .SLEW("SLOW")
        ) IOBUF_inst_LDM (
                // .O() intentionally not connected
                .IO(sd_LDM_O),
                .I(LDM_conn),
                .T(1'b0)
        );

         */

        //
        // data strobe pins (LDQS, UDQS) and corresponding IOBUFs and ODDRs
        //

        RtlBitSignal dqsState = controllerCore.createBitOutputPort("ddrInterfaceDataStrobe");
        RtlBitSignal dqsThreestate = controllerCore.createBitOutputPort("ddrInterfaceDataStrobeEnable").not();
        {
            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", ddrClk180);
            oddr.createBitInputPort("C1", ddrClk0);
            oddr.createBitInputPort("CE", true);
            oddr.createBitInputPort("R", false);
            oddr.createBitInputPort("S", false);
            oddr.createBitInputPort("D0", dqsState);
            oddr.createBitInputPort("D1", false);
            RtlBitSignal outputDdrBit = oddr.createBitOutputPort("Q");

            RtlModuleInstance iobuf = new RtlModuleInstance(realm, "IOBUF");
            iobuf.getParameters().put("DRIVE", 4);
            iobuf.getParameters().put("IBUF_DELAY_VALUE", "0");
            iobuf.getParameters().put("IFD_DELAY_VALUE", "AUTO");
            iobuf.getParameters().put("IOSTANDARD", "DEFAULT");
            iobuf.getParameters().put("SLEW", "SLOW");
            // port "O" (input from SDRAM) intentionally not connected like in the original controller, but actually
            // this should be fed to a DCM, phase-shifted and used to sample read data
            iobuf.createBitInputPort("I", outputDdrBit);
            iobuf.createBitInputPort("T", dqsThreestate);
            ramBidirectionalPin(realm, "G3", iobuf, "IO");
        }
        {
            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", ddrClk180);
            oddr.createBitInputPort("C1", ddrClk0);
            oddr.createBitInputPort("CE", true);
            oddr.createBitInputPort("R", false);
            oddr.createBitInputPort("S", false);
            oddr.createBitInputPort("D0", dqsState);
            oddr.createBitInputPort("D1", false);
            RtlBitSignal outputDdrBit = oddr.createBitOutputPort("Q");

            RtlModuleInstance iobuf = new RtlModuleInstance(realm, "IOBUF");
            iobuf.getParameters().put("DRIVE", 4);
            iobuf.getParameters().put("IBUF_DELAY_VALUE", "0");
            iobuf.getParameters().put("IFD_DELAY_VALUE", "AUTO");
            iobuf.getParameters().put("IOSTANDARD", "DEFAULT");
            iobuf.getParameters().put("SLEW", "SLOW");
            // port "O" (input from SDRAM) intentionally not connected like in the original controller, but actually
            // this should be fed to a DCM, phase-shifted and used to sample read data
            iobuf.createBitInputPort("I", outputDdrBit);
            iobuf.createBitInputPort("T", dqsThreestate);
            ramBidirectionalPin(realm, "L6", iobuf, "IO");
        }

        //
        // other stuff
        //

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
        // ramBidirectionalPin(realm, "G3", controllerCore, "sd_UDQS_IO");
        // ramBidirectionalPin(realm, "L6", controllerCore, "sd_LDQS_IO");

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
