package name.martingeisse.esdk.examples.moving_light;

import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;

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
