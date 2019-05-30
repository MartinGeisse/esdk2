package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.riscv.rtl.terminal.*;

import javax.swing.*;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
		ComputerDesign design = new ComputerDesign() {
			@Override
			protected ComputerModule.Implementation createComputerModule() {
				return new ComputerModule.Implementation(getRealm(), getClock()) {

					@Override
					protected TextDisplayController createTextDisplay() {
						// return new SimulatedTextDisplayController(getRealm(), getClock());
						return super.createTextDisplay();
					}

					@Override
					protected KeyboardController createKeyboard() {
						// return new SimulatedKeyboardController(getRealm(), getClock());
						return new UnconnectedKeyboard(getRealm());
					}

				};
			}
		};
		ComputerModule.Implementation computerModule = design.getComputerModule();
		RtlRealm realm = design.getRealm();

		design.getClockSignalConnector().setConnected(new RtlBitConstant(realm, false));
		new RtlClockGenerator(design.getClock(), 10);
		computerModule.setExternalReset(new RtlBitConstant(realm, false));
		// prepareHighlevelDisplaySimulation(design);
		prepareHdlDisplaySimulation(design);

		design.simulate();
	}

	private static void prepareHighlevelDisplaySimulation(ComputerDesign design) {
		TerminalPanel terminalPanel = new TerminalPanel(design.getClock());
		((SimulatedTextDisplayController)design.getComputerModule()._textDisplay).setTerminalPanel(terminalPanel);
		((SimulatedKeyboardController)design.getComputerModule()._keyboard).setTerminalPanel(terminalPanel);

		JFrame frame = new JFrame("Terminal");
		frame.add(terminalPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> terminalPanel.repaint()).start();
	}

	private static void prepareHdlDisplaySimulation(ComputerDesign design) {
		ComputerModule.Implementation computerModule = design.getComputerModule();
		MyMonitorPanel monitorPanel = new MyMonitorPanel(design.getClock(), (TextDisplayController.Implementation) computerModule._textDisplay);

		JFrame frame = new JFrame("Terminal");
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> monitorPanel.repaint()).start();
	}

}
