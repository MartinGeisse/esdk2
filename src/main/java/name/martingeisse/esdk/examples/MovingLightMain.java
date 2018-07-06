package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRegion;
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

		Design design = new Design();
		RtlRegion region = new RtlRegion(design);
		RtlClockNetwork clk = region.createClockNetwork(clockPin(region));

		RtlClockedBlock block = clk.createBlock();

		RtlProceduralVectorSignal prescaler = block.createVector(24);
		block.getInitializerStatements().assignUnsigned(prescaler, 0);
		block.getStatements().assign(prescaler, prescaler.add(1));

		RtlProceduralVectorSignal leds = block.createVector(8);
		block.getInitializerStatements().assignUnsigned(leds, 1);
		RtlWhenStatement whenPrescalerZero = block.getStatements().when(prescaler.compareEqual(0));
		whenPrescalerZero.getThenBranch().assign(leds, new RtlConcatenation(region,
			leds.select(0),
			leds.select(7, 1)
		));

		ledPin(region, "F12", leds.select(0));
		ledPin(region, "E12", leds.select(1));
		ledPin(region, "E11", leds.select(2));
		ledPin(region, "F11", leds.select(3));
		ledPin(region, "C11", leds.select(4));
		ledPin(region, "D11", leds.select(5));
		ledPin(region, "E9", leds.select(6));
		ledPin(region, "F9", leds.select(7));

		new ProjectGenerator(region, "MovingLight", new File("ise/moving_light"), "XC3S500E-FG320-4").generate();
	}

	private static RtlOutputPin ledPin(RtlRegion region, String id, RtlBitSignal outputSignal) {
		RtlOutputPin pin = ledPin(region, id);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlOutputPin ledPin(RtlRegion region, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
		configuration.setDrive(8);
		RtlOutputPin pin = new RtlOutputPin(region);
		pin.setId(id);
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlInputPin clockPin(RtlRegion region) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(region);
		pin.setId("C9");
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

}
