package name.martingeisse.esdk.riscv.rtl.terminal;

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
public class SimulatedTextDisplayController extends RtlSimulationItem implements TextDisplayController {

	private TerminalPanel terminalPanel;
	private RtlBitSignalConnector clockEnable;
	private RtlBitSignalConnector writeEnable;
	private RtlVectorSignalConnector address;
	private RtlVectorSignalConnector writeData;

	public SimulatedTextDisplayController(RtlRealm realm, RtlClockNetwork ignored) {
		super(realm);
		this.clockEnable = new RtlBitSignalConnector(realm);
		this.writeEnable = new RtlBitSignalConnector(realm);
		this.address = new RtlVectorSignalConnector(realm, 12);
		this.writeData = new RtlVectorSignalConnector(realm, 8);
		setTerminalPanel(null);
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTerminalPanel(TerminalPanel terminalPanel) {
		this.terminalPanel = terminalPanel;
		if (terminalPanel != null) {
			terminalPanel.getCharacterMatrixPort().setClockEnableSignal(clockEnable);
			terminalPanel.getCharacterMatrixPort().setWriteEnableSignal(writeEnable);
			terminalPanel.getCharacterMatrixPort().setAddressSignal(address);
			terminalPanel.getCharacterMatrixPort().setWriteDataSignal(writeData);
		}
	}

	public RtlBitSignal getClockEnable() {
		return clockEnable.getConnected();
	}

	public void setClockEnable(RtlBitSignal clockEnableSignal) {
		clockEnable.setConnected(clockEnableSignal);
	}

	public RtlBitSignal getWriteEnable() {
		return writeEnable.getConnected();
	}

	public void setWriteEnable(RtlBitSignal writeEnableSignal) {
		writeEnable.setConnected(writeEnableSignal);
	}

	public RtlVectorSignal getAddress() {
		return address.getConnected();
	}

	public void setAddress(RtlVectorSignal addressSignal) {
		address.setConnected(addressSignal);
	}

	public RtlVectorSignal getWriteData() {
		return writeData.getConnected();
	}

	public void setWriteData(RtlVectorSignal writeDataSignal) {
		writeData.setConnected(writeDataSignal);
	}

}
