package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.xilinx.XilinxPinConfiguration;

import java.io.File;

/**
 *
 */
public class MovingLightMain {

	public static void main(String[] args) throws Exception {

		Design mainDesign = new Design();
		RtlDesign design = new RtlDesign(mainDesign);
		RtlClockNetwork clk = design.createClockNetwork(clockPin(design));

		RtlClockedBlock block = clk.createBlock();

		RtlProceduralVectorSignal prescaler = block.createVector(24);
		block.getInitializerStatements().assignUnsigned(prescaler, 0);
		block.getStatements().assign(prescaler, prescaler.add(1));

		RtlProceduralVectorSignal leds = block.createVector(8);
		block.getInitializerStatements().assignUnsigned(leds, 1);
		RtlWhenStatement whenPrescalerZero = block.getStatements().when(prescaler.compareEqual(0));
		whenPrescalerZero.getThenBranch().assign(leds, new RtlConcatenation(design,
			leds.select(0),
			leds.select(7, 1)
		));

		ledPin(design, "F12", leds.select(0));
		ledPin(design, "E12", leds.select(1));
		ledPin(design, "E11", leds.select(2));
		ledPin(design, "F11", leds.select(3));
		ledPin(design, "C11", leds.select(4));
		ledPin(design, "D11", leds.select(5));
		ledPin(design, "E9", leds.select(6));
		ledPin(design, "F9", leds.select(7));

		new ProjectGenerator(design, "MovingLight", new File("ise/moving_light"), "XC3S500E-FG320-4").generate();
	}

	private static RtlOutputPin ledPin(RtlDesign design, String id, RtlBitSignal outputSignal) {
		RtlOutputPin pin = ledPin(design, id);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlOutputPin ledPin(RtlDesign design, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
		configuration.setDrive(8);
		RtlOutputPin pin = new RtlOutputPin(design);
		pin.setId(id);
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlInputPin clockPin(RtlDesign design) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(design);
		pin.setId("C9");
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

}
