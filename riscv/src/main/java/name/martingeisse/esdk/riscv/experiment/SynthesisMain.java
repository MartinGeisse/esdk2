package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;

import java.io.File;

/**
 * Writing to the display in hardware works (see DisplayTest.mahdl). The CPU doesn't, not even with a super-simple
 * test problem that sticks to the simplest instructions and avoids the x0 register.
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {
		Computer design = new Computer();
		SimulationMain.loadProgram(design.getComputerModule());
		new ProjectGenerator(design.getRealm(), "TerminalTest", new File("ise/terminal_test"), "XC3S500E-FG320-4").generate();
	}

}
