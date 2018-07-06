package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class VgaTestPatternMain {

	public static void main(String[] args) throws Exception {
		// TODO
		Design design = new Design();
		RtlDomain domain = new RtlDomain(design);
		new ProjectGenerator(domain, "EsdkTestbild", new File("ise"), "XC3S500E-FG320-4").generate();
	}

}
