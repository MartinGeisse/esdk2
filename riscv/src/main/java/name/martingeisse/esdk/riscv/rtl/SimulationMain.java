package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.riscv.experiment.ComputerModule;
import name.martingeisse.esdk.riscv.rtl.terminal.MyMonitorPanel;
import name.martingeisse.esdk.riscv.experiment.terminal.TextDisplayController;

import javax.swing.*;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
		ComputerDesign design = new ComputerDesign();
		new RtlClockGenerator(design.getClock(), 10);

//		TerminalPanel terminalPanel = new TerminalPanel(design.getClock());
//		design.getComputerModule()._textDisplay.setTerminalPanel(terminalPanel);
//		design.getComputerModule()._keyboard.setTerminalPanel(terminalPanel);

		ComputerModule.Implementation computerModule = (ComputerModule.Implementation)design.getComputerModule();
		MyMonitorPanel monitorPanel = new MyMonitorPanel(design.getClock(), (TextDisplayController.Implementation) computerModule._textDisplay);

		JFrame frame = new JFrame("Terminal");
		// frame.add(terminalPanel);
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		// new Timer(500, event -> terminalPanel.repaint()).start();
		new Timer(500, event -> monitorPanel.repaint()).start();

		design.simulate();
	}


}
