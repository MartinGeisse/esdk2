package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class DisplayTestSynthesisMain {

	public static void main(String[] args) throws Exception {
		DisplayTestDesign design = new DisplayTestDesign();
		DisplayTestSimulationMain.loadProgram(design.getDisplayTest());
		new ProjectGenerator(design.getRealm(), "DisplayTest", new File("ise/display_test"), "XC3S500E-FG320-4").generate();
	}

}
