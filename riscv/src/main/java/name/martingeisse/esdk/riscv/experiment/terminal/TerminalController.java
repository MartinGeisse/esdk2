package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public class TerminalController extends RtlItem {

	private final TerminalPanel terminalPanel;

	public TerminalController(RtlRealm realm, RtlClockNetwork clockNetwork) {
		super(realm);
		this.terminalPanel = new TerminalPanel(clockNetwork);
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public RtlVectorSignal getReadData() {
		return terminalPanel.getCharacterMatrixPort().getReadDataSignal();
	}

	public RtlBitSignal getClockEnable() {
		return terminalPanel.getCharacterMatrixPort().getClockEnableSignal();
	}

	public void setClockEnable(RtlBitSignal clockEnableSignal) {
		terminalPanel.getCharacterMatrixPort().setClockEnableSignal(clockEnableSignal);
	}

	public RtlBitSignal getWriteEnable() {
		return terminalPanel.getCharacterMatrixPort().getWriteEnableSignal();
	}

	public void setWriteEnable(RtlBitSignal writeEnableSignal) {
		terminalPanel.getCharacterMatrixPort().setWriteEnableSignal(writeEnableSignal);
	}

	public RtlVectorSignal getAddress() {
		return terminalPanel.getCharacterMatrixPort().getAddressSignal();
	}

	public void setAddress(RtlVectorSignal addressSignal) {
		terminalPanel.getCharacterMatrixPort().setAddressSignal(addressSignal);
	}

	public RtlVectorSignal getWriteData() {
		return terminalPanel.getCharacterMatrixPort().getWriteDataSignal();
	}

	public void setWriteData(RtlVectorSignal writeDataSignal) {
		terminalPanel.getCharacterMatrixPort().setWriteDataSignal(writeDataSignal);
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
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
