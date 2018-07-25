package name.martingeisse.esdk.examples.moving_light;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.xilinx.XilinxPinConfiguration;

import java.io.File;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {
		MovingLightDesign design = new MovingLightDesign();
		new ProjectGenerator(design.getRealm(), "MovingLight", new File("ise/moving_light"), "XC3S500E-FG320-4").generate();
	}

}
