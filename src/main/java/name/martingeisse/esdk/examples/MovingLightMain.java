package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlDomain;
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
		RtlDomain domain = new RtlDomain(design);
		RtlClockNetwork clk = domain.createClockNetwork(clockPin(domain));

		RtlClockedBlock block = clk.createBlock();

		RtlProceduralVectorSignal prescaler = block.createVector(24);
		block.getInitializerStatements().assignUnsigned(prescaler, 0);
		block.getStatements().assign(prescaler, prescaler.add(1));

		RtlProceduralVectorSignal leds = block.createVector(8);
		block.getInitializerStatements().assignUnsigned(leds, 1);
		RtlWhenStatement whenPrescalerZero = block.getStatements().when(prescaler.compareEqual(0));
		whenPrescalerZero.getThenBranch().assign(leds, new RtlConcatenation(domain,
			leds.select(0),
			leds.select(7, 1)
		));

		ledPin(domain, "F12", leds.select(0));
		ledPin(domain, "E12", leds.select(1));
		ledPin(domain, "E11", leds.select(2));
		ledPin(domain, "F11", leds.select(3));
		ledPin(domain, "C11", leds.select(4));
		ledPin(domain, "D11", leds.select(5));
		ledPin(domain, "E9", leds.select(6));
		ledPin(domain, "F9", leds.select(7));

		new ProjectGenerator(domain, "MovingLight", new File("ise/moving_light"), "XC3S500E-FG320-4").generate();
	}

	private static RtlOutputPin ledPin(RtlDomain domain, String id, RtlBitSignal outputSignal) {
		RtlOutputPin pin = ledPin(domain, id);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlOutputPin ledPin(RtlDomain domain, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
		configuration.setDrive(8);
		RtlOutputPin pin = new RtlOutputPin(domain);
		pin.setId(id);
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlInputPin clockPin(RtlDomain domain) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(domain);
		pin.setId("C9");
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

}
