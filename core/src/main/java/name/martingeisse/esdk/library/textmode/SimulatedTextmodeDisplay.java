package name.martingeisse.esdk.library.textmode;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 *
 */
public class SimulatedTextmodeDisplay extends RtlItem {

	private final TextmodePanel textmodePanel;

	public SimulatedTextmodeDisplay(RtlRealm realm, RtlClockNetwork clockNetwork) {
		super(realm);
		this.textmodePanel = new TextmodePanel(clockNetwork);
	}

	public TextmodePanel getTextmodePanel() {
		return textmodePanel;
	}

	public RtlVectorSignal getReadData() {
		return textmodePanel.getCharacterMatrixPort().getReadDataSignal();
	}

	public RtlBitSignal getClockEnable() {
		return textmodePanel.getCharacterMatrixPort().getClockEnableSignal();
	}

	public void setClockEnable(RtlBitSignal clockEnableSignal) {
		textmodePanel.getCharacterMatrixPort().setClockEnableSignal(clockEnableSignal);
	}

	public RtlBitSignal getWriteEnable() {
		return textmodePanel.getCharacterMatrixPort().getWriteEnableSignal();
	}

	public void setWriteEnable(RtlBitSignal writeEnableSignal) {
		textmodePanel.getCharacterMatrixPort().setWriteEnableSignal(writeEnableSignal);
	}

	public RtlVectorSignal getAddress() {
		return textmodePanel.getCharacterMatrixPort().getAddressSignal();
	}

	public void setAddress(RtlVectorSignal addressSignal) {
		textmodePanel.getCharacterMatrixPort().setAddressSignal(addressSignal);
	}

	public RtlVectorSignal getWriteData() {
		return textmodePanel.getCharacterMatrixPort().getWriteDataSignal();
	}

	public void setWriteData(RtlVectorSignal writeDataSignal) {
		textmodePanel.getCharacterMatrixPort().setWriteDataSignal(writeDataSignal);
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
