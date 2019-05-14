package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;

/**
 *
 */
public class SimulatedKeyboardController extends RtlSimulationItem {

	private TerminalPanel terminalPanel;

	public SimulatedKeyboardController(RtlRealm realm, RtlClockNetwork ignored) {
		super(realm);
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTerminalPanel(TerminalPanel terminalPanel) {
		this.terminalPanel = terminalPanel;
	}

	public RtlVectorSignal getInputData() {
		return terminalPanel.getInputDataSignal();
	}

	public RtlBitSignal getInputAcknowledge() {
		return terminalPanel.getInputAcknowledgeSignal();
	}

	public void setInputAcknowledge(RtlBitSignal inputAcknowledgeSignal) {
		terminalPanel.setInputAcknowledgeSignal(inputAcknowledgeSignal);
	}

}
