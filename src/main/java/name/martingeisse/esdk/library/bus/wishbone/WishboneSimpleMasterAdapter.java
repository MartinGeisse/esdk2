package name.martingeisse.esdk.library.bus.wishbone;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 *
 */
public class WishboneSimpleMasterAdapter extends RtlItem implements WishboneSimpleMaster {

	private final RtlBitSignalConnector cycleStrobeSignal;
	private final RtlBitSignalConnector writeEnableSignal;
	private final RtlVectorSignalConnector addressSignal;
	private final RtlVectorSignalConnector writeDataSignal;
	private final RtlVectorSignalConnector readDataSignal;
	private final RtlBitSignalConnector ackSignal;

	public WishboneSimpleMasterAdapter(RtlRealm realm) {
		super(realm);
		cycleStrobeSignal = new RtlBitSignalConnector(realm);
		writeEnableSignal = new RtlBitSignalConnector(realm);
		addressSignal = new RtlVectorSignalConnector(realm, 32);
		writeDataSignal = new RtlVectorSignalConnector(realm, 32);
		readDataSignal = new RtlVectorSignalConnector(realm, 32);
		ackSignal = new RtlBitSignalConnector(realm);
	}

	@Override
	public RtlBitSignal getCycleStrobeSignal() {
		return cycleStrobeSignal;
	}

	public void setCycleStrobeSignal(RtlBitSignal cycleStrobeSignal) {
		this.cycleStrobeSignal.setConnected(cycleStrobeSignal);
	}

	@Override
	public RtlBitSignal getWriteEnableSignal() {
		return writeEnableSignal;
	}

	public void setWriteEnableSignal(RtlBitSignal writeEnableSignal) {
		this.writeEnableSignal.setConnected(writeEnableSignal);
	}

	@Override
	public RtlVectorSignal getAddressSignal() {
		return addressSignal;
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		this.addressSignal.setConnected(addressSignal);
	}

	@Override
	public RtlVectorSignal getWriteDataSignal() {
		return writeDataSignal;
	}

	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		this.writeDataSignal.setConnected(writeDataSignal);
	}

	public RtlVectorSignal getReadDataSignal() {
		return readDataSignal;
	}

	@Override
	public void setReadDataSignal(RtlVectorSignal readDataSignal) {
		this.readDataSignal.setConnected(readDataSignal);
	}

	public RtlBitSignal getAckSignal() {
		return ackSignal;
	}

	@Override
	public void setAckSignal(RtlBitSignal ackSignal) {
		this.ackSignal.setConnected(ackSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
