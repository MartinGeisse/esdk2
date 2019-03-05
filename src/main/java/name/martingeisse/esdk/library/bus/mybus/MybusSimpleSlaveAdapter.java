package name.martingeisse.esdk.library.bus.mybus;

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
public class MybusSimpleSlaveAdapter extends RtlItem implements MybusSlave {

	private final RtlBitSignalConnector strobeSignal;
	private final RtlBitSignalConnector writeEnableSignal;
	private final RtlVectorSignalConnector addressSignal;
	private final RtlVectorSignalConnector writeDataSignal;
	private final RtlVectorSignalConnector readDataSignal;
	private final RtlBitSignalConnector ackSignal;

	public MybusSimpleSlaveAdapter(RtlRealm realm) {
		super(realm);
		strobeSignal = new RtlBitSignalConnector(realm);
		writeEnableSignal = new RtlBitSignalConnector(realm);
		addressSignal = new RtlVectorSignalConnector(realm, 32);
		writeDataSignal = new RtlVectorSignalConnector(realm, 32);
		readDataSignal = new RtlVectorSignalConnector(realm, 32);
		ackSignal = new RtlBitSignalConnector(realm);
	}

	public RtlBitSignal getStrobeSignal() {
		return strobeSignal;
	}

	@Override
	public void setStrobeSignal(RtlBitSignal strobeSignal) {
		this.strobeSignal.setConnected(strobeSignal);
	}

	public RtlBitSignal getWriteEnableSignal() {
		return writeEnableSignal;
	}

	@Override
	public void setWriteEnableSignal(RtlBitSignal writeEnableSignal) {
		this.writeEnableSignal.setConnected(writeEnableSignal);
	}

	public RtlVectorSignal getAddressSignal() {
		return addressSignal;
	}

	@Override
	public void setAddressSignal(RtlVectorSignal addressSignal) {
		this.addressSignal.setConnected(addressSignal);
	}

	public RtlVectorSignal getWriteDataSignal() {
		return writeDataSignal;
	}

	@Override
	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		this.writeDataSignal.setConnected(writeDataSignal);
	}

	@Override
	public RtlVectorSignal getReadDataSignal() {
		return readDataSignal;
	}

	public void setReadDataSignal(RtlVectorSignal readDataSignal) {
		this.readDataSignal.setConnected(readDataSignal);
	}

	@Override
	public RtlBitSignal getAckSignal() {
		return ackSignal;
	}

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
