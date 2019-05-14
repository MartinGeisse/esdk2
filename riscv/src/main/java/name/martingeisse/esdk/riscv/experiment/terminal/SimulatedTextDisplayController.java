package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;

/**
 *
 */
public class SimulatedTextDisplayController extends RtlSimulationItem {

	private TerminalPanel terminalPanel;

	public SimulatedTextDisplayController(RtlRealm realm, RtlClockNetwork ignored) {
		super(realm);
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTerminalPanel(TerminalPanel terminalPanel) {
		this.terminalPanel = terminalPanel;
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

}
