package name.martingeisse.esdk.riscv.rtl.ram;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.*;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConstantIndexSelection;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;

public class SdramConnectorImpl extends SdramConnector.Implementation {

    public SdramConnectorImpl(RtlRealm realm,
                              RtlClockNetwork clk0,
                              RtlClockNetwork clk180,
                              RtlClockNetwork clk270,
                              RtlClockNetwork clk90) {
        super(realm, clk0, clk180, clk270, clk90);

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
        sdramCkpDdr.createBitInputPort("C0", clk180.getClockSignal());
        sdramCkpDdr.createBitInputPort("C1", clk0.getClockSignal());
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
        sdramCknDdr.createBitInputPort("C0", clk0.getClockSignal());
        sdramCknDdr.createBitInputPort("C1", clk180.getClockSignal());
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
        RtlBitSignal ddrInterfaceDataOutThreestate = _ddrInterfaceDataOutEnable.not();
        RtlVectorSignal ddrInterfaceDataOut = _ddrInterfaceDataOut;
        for (int i = 0; i < 16; i++) {
            int finalI = i;

            RtlBitSignalConnector inputDdrBitConnector = new RtlBitSignalConnector(realm);

            // Sampling at clk0 and clk180 seems wrong to me. The original ddr_sdram code said in a comment that
            // with CL=2 the data could for some strange reason be sampled at 2.5 cycles after issuing a READ
            // command, but it does not say whether the author expected 2 cycles or 2.25 cycles to be correct.
            // The DDR spec would demand 2.25 cycles -- 2 cycles after the READ comes the positive DQS edge, 0.25
            // cycles after that is the ideal time to sample read data. It would be relevant whether it was 2.0 or
            // 2.25 that did not work for the original module; 2.0 would indicate a misunderstanding about
            // DDR timing, while 2.25 would indicate a problem with how the SDRAM chip works or is connected.
            //
            // Note, though, that the above is only correct for the timing at the RAM chip pins. If you add the return
            // delay back to the FPGA, things may look different. For example, assume that the return delay is
            // around 2.5ns (0.25 cycles), then shifting DQS by another 0.25 cycles adds up to the 0.5 cycles that
            // worked for the original controller.
            //
            // Note that the timing diagrams make DQS *look* like a clock-enable, but it is actually a clock signal
            // that must be phase-shifted for reading.
            RtlModuleInstance iddr = new RtlModuleInstance(realm, "IDDR2");
            iddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            iddr.getParameters().put("INIT_Q0", VectorValue.of(1, 0));
            iddr.getParameters().put("INIT_Q1", VectorValue.of(1, 0));
            iddr.getParameters().put("SRTYPE", "SYNC");
            iddr.createBitInputPort("C0", clk180.getClockSignal());
            iddr.createBitInputPort("C1", clk0.getClockSignal());
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
            oddr.createBitInputPort("C0", clk90.getClockSignal());
            oddr.createBitInputPort("C1", clk270.getClockSignal());
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
        _ddrInterfaceDataIn.setConnected(upperDataBits.concat(lowerDataBits));

        //
        // write data masks (follow the same timing as write data) and corresponding IOBUFs and ODDRs
        //

        RtlVectorSignal ddrInterfaceDataOutMask = _ddrInterfaceDataOutMask;
        { // UDM
            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", clk90.getClockSignal());
            oddr.createBitInputPort("C1", clk270.getClockSignal());
            oddr.createBitInputPort("CE", true);
            oddr.createBitInputPort("R", false);
            oddr.createBitInputPort("S", false);
            oddr.createBitInputPort("D0", ddrInterfaceDataOutMask.select(1));
            oddr.createBitInputPort("D1", ddrInterfaceDataOutMask.select(3));
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
            iobuf.createBitInputPort("T", false);
            ramBidirectionalPin(realm, "J1", iobuf, "IO");
        }
        { // LDM
            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", clk90.getClockSignal());
            oddr.createBitInputPort("C1", clk270.getClockSignal());
            oddr.createBitInputPort("CE", true);
            oddr.createBitInputPort("R", false);
            oddr.createBitInputPort("S", false);
            oddr.createBitInputPort("D0", ddrInterfaceDataOutMask.select(0));
            oddr.createBitInputPort("D1", ddrInterfaceDataOutMask.select(2));
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
            iobuf.createBitInputPort("T", false);
            ramBidirectionalPin(realm, "J2", iobuf, "IO");
        }

        //
        // data strobe pins (LDQS, UDQS) and corresponding IOBUFs and ODDRs
        //

        RtlBitSignal dqsState = _ddrInterfaceDataStrobe;
        RtlBitSignal dqsThreestate = _ddrInterfaceDataStrobeEnable.not();
        { // UDQS
            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", clk180.getClockSignal());
            oddr.createBitInputPort("C1", clk0.getClockSignal());
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
        { // LDQS
            RtlModuleInstance oddr = new RtlModuleInstance(realm, "ODDR2");
            oddr.getParameters().put("DDR_ALIGNMENT", "NONE");
            oddr.getParameters().put("INIT", VectorValue.of(1, 0));
            oddr.getParameters().put("SRTYPE", "SYNC");
            oddr.createBitInputPort("C0", clk180.getClockSignal());
            oddr.createBitInputPort("C1", clk0.getClockSignal());
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

        // SDRAM DDR interface
        ramOutputPin(realm, "K4", _sdramCS);
        ramOutputPin(realm, "K3", _sdramCKE);
        ramOutputPin(realm, "C1", _sdramRAS);
        ramOutputPin(realm, "C2", _sdramCAS);
        ramOutputPin(realm, "D1", _sdramWE);
        ramOutputPinArray(realm, _sdramA,
                "T1", "R3", "R2", "P1", "F4", "H4", "H3", "H1", "H2", "N4", "T2", "N5", "P2");
        ramOutputPinArray(realm, _sdramBA, "K5", "K6");

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
