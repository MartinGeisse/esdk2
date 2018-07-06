package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlRegion;
import name.martingeisse.esdk.core.rtl.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class VgaTestPatternMain {

	public static void main(String[] args) throws Exception {
		// TODO
		Design design = new Design();
		RtlRegion region = new RtlRegion(design);
		new ProjectGenerator(region, "EsdkTestbild", new File("ise"), "XC3S500E-FG320-4").generate();
	}

}
