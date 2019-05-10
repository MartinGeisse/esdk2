package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;

import javax.swing.*;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
		Computer design = new Computer();
		new RtlClockGenerator(design.getClock(), 10);

		JFrame frame = new JFrame("Textmode");
		frame.add(design.getComputerModule().display.getTextmodePanel());
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		design.simulate();
	}

}
