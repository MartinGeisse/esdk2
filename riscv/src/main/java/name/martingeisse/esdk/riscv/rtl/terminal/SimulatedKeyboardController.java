package name.martingeisse.esdk.riscv.rtl.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public class SimulatedKeyboardController extends RtlSimulationItem implements KeyboardController {

	private TerminalPanel terminalPanel;
	private RtlVectorSignalConnector inputData;
	private RtlBitSignalConnector inputAcknowledge;

	public SimulatedKeyboardController(RtlRealm realm, RtlClockNetwork ignored) {
		super(realm);
		this.inputData = new RtlVectorSignalConnector(realm, 8);
		this.inputAcknowledge = new RtlBitSignalConnector(realm);
		setTerminalPanel(null);
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTerminalPanel(TerminalPanel terminalPanel) {
		this.terminalPanel = terminalPanel;
		if (terminalPanel == null) {
			inputData.setConnected(new RtlVectorConstant(getRealm(), VectorValue.of(8, 0)));
		} else {
			inputData.setConnected(terminalPanel.getInputDataSignal());
			terminalPanel.setInputAcknowledgeSignal(inputAcknowledge);
		}
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
