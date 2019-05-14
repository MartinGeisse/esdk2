package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;

/**
 *
 */
public class SimulatedKeyboardController extends RtlSimulationItem {

	private TerminalPanel terminalPanel;
	private RtlVectorSignalConnector inputData;
	private RtlBitSignalConnector inputAcknowledge;

	public SimulatedKeyboardController(RtlRealm realm, RtlClockNetwork ignored) {
		super(realm);
		this.inputData = new RtlVectorSignalConnector(realm, 8);
		this.inputAcknowledge = new RtlBitSignalConnector(realm);
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTerminalPanel(TerminalPanel terminalPanel) {
		this.terminalPanel = terminalPanel;
		inputData.setConnected(terminalPanel.getInputDataSignal());
		terminalPanel.setInputAcknowledgeSignal(inputAcknowledge);
	}

	public RtlVectorSignal getInputData() {
		return inputData;
	}

	public RtlBitSignal getInputAcknowledge() {
		return inputAcknowledge.getConnected();
	}

	public void setInputAcknowledge(RtlBitSignal inputAcknowledgeSignal) {
		inputAcknowledge.setConnected(inputAcknowledgeSignal);
	}

}
