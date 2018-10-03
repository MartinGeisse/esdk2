package name.martingeisse.esdk.examples.pico_blink;

import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.examples.moving_light.MovingLightDesign;

import java.io.File;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {
		PicoBlinkDesign design = new PicoBlinkDesign();
		new ProjectGenerator(design.getRealm(), "PicoBlink", new File("ise/pico_blink"), "XC3S500E-FG320-4").generate();
	}

}
