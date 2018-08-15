package name.martingeisse.esdk.examples.vga;

import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.library.vga.Monitor;
import name.martingeisse.esdk.library.vga.MonitorPanel;

import javax.swing.*;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {

		VgaTestPatternDesign design = new VgaTestPatternDesign();
		new RtlClockGenerator(design.getClock(), 10);

		MonitorPanel monitorPanel = new MonitorPanel(design.getClock(), 640, 480);
		Monitor monitor = monitorPanel.getMonitor();
		monitor.setR(design.getR().getOutputSignal().asOneBitVector());
		monitor.setG(design.getG().getOutputSignal().asOneBitVector());
		monitor.setB(design.getB().getOutputSignal().asOneBitVector());
		monitor.setHsync(design.getHsync().getOutputSignal());
		monitor.setVsync(design.getVsync().getOutputSignal());

		JFrame frame = new JFrame("VGA Test Pattern");
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		design.simulate();
	}

}
