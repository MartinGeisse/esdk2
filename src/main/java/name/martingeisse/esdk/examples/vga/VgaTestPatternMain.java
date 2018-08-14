package name.martingeisse.esdk.examples.vga;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class VgaTestPatternMain {

	public static void main(String[] args) throws Exception {
		// TODO
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		new ProjectGenerator(realm, "EsdkTestbild", new File("ise"), "XC3S500E-FG320-4").generate();
	}

}
