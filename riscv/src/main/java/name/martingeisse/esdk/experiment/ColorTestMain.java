package name.martingeisse.esdk.experiment;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.io.File;

/**
 *
 */
public class ColorTestMain {

    public static void main(String[] args) throws Exception {
        Design design = new Design();
        RtlRealm realm = new RtlRealm(design);

        // clk / reset
        RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset");
        clkReset.createBitInputPort("clk_in", clockPin(realm));
        clkReset.createBitInputPort("reset_in", buttonPin(realm, "V16"));
        RtlBitSignal reset = clkReset.createBitOutputPort("reset");
        RtlClockNetwork clk0 = realm.createClockNetwork(withName(clkReset.createBitOutputPort("ddr_clk_0"), "ddr_clk_0"));

        // main module
        ColorTest colorTest = new ColorTest.Implementation(realm, clk0);

        // VGA pins.
        // Note that the "first" clock is the clk180 because the color signals generated by the ColorTest module change
        // with the clk0 edge, so the clk180 edge is the first edge to occur after that.
        vgaPin(realm, "H14",  colorTest.getR().select(2));
        vgaPin(realm, "H15",  colorTest.getG().select(2));
        vgaPin(realm, "G15",  colorTest.getB().select(2));
        vgaPin(realm, "F15", colorTest.getHsync());
        vgaPin(realm, "F14", colorTest.getVsync());

        ProjectGenerator projectGenerator = new ProjectGenerator(realm, "ColorTest", new File("ise/color_test"), "XC3S500E-FG320-4");
        projectGenerator.addVerilogFile(new File("riscv/resource/hdl/clk_reset.v"));
        projectGenerator.addUcfLine("NET \"pinC9\" PERIOD = 20.0ns HIGH 40%;");
        projectGenerator.addUcfLine("CONFIG PROHIBIT = D2;");
        projectGenerator.addUcfLine("CONFIG PROHIBIT = G4;");
        projectGenerator.addUcfLine("CONFIG PROHIBIT = J6;");
        projectGenerator.addUcfLine("CONFIG PROHIBIT = L5;");
        projectGenerator.addUcfLine("CONFIG PROHIBIT = R4;");
        projectGenerator.generate();
    }

    private static <T extends Item> T withName(T item, String name) {
        item.setName(name);
        return item;
    }

    private static RtlInputPin clockPin(RtlRealm realm) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("LVCMOS33");
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId("C9");
        pin.setConfiguration(configuration);
        return pin;
    }

    private static RtlInputPin buttonPin(RtlRealm realm, String id) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("LVTTL");
        configuration.setAdditionalInfo("PULLDOWN");
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        return pin;
    }

    private static void vgaPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard("LVTTL");
        configuration.setDrive(8);
        configuration.setSlew(XilinxPinConfiguration.Slew.FAST);
        RtlOutputPin pin = new RtlOutputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        pin.setOutputSignal(outputSignal);
    }

}
