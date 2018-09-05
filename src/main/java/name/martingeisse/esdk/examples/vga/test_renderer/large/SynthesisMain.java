package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.examples.moving_light.MovingLightDesign;
import name.martingeisse.esdk.examples.vga.test_renderer.FramebufferDisplay;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {

		BufferedImage framebuffer = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		TestRendererDesign design = new TestRendererDesign(10, 9);
		FramebufferDisplay display = new FramebufferDisplay(design.getClock(), framebuffer, 10);
		design.connectDisplay(display);
		new ProjectGenerator(design.getRealm(), "MovingLight", new File("ise/test_renderer"), "XC3S500E-FG320-4").generate();
	}

}
