package name.martingeisse.esdk.library.mybus.rtl;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 * Directly connects a single master to a single slave.
 */
public final class RtlMybusOneToOneConnector extends RtlItem {

	private final RtlBitSignalConnector strobeSignal;
	private final RtlBitSignalConnector writeEnableSignal;
	private final RtlVectorSignalConnector addressSignal;
	private final RtlVectorSignalConnector writeDataSignal;
	private final RtlVectorSignalConnector readDataSignal;
	private final RtlBitSignalConnector ackSignal;

	public RtlMybusOneToOneConnector(RtlRealm realm) {
		super(realm);
		strobeSignal = new RtlBitSignalConnector(realm);
		writeEnableSignal = new RtlBitSignalConnector(realm);
		addressSignal = new RtlVectorSignalConnector(realm, 32);
		writeDataSignal = new RtlVectorSignalConnector(realm, 32);
		readDataSignal = new RtlVectorSignalConnector(realm, 32);
		ackSignal = new RtlBitSignalConnector(realm);
	}

	public void connectMaster(RtlMybusMaster master) {
		strobeSignal.setConnected(master.getStrobeSignal());
		writeEnableSignal.setConnected(master.getWriteEnableSignal());
		addressSignal.setConnected(master.getAddressSignal());
		writeDataSignal.setConnected(master.getWriteDataSignal());
		master.setReadDataSignal(readDataSignal);
		master.setAckSignal(ackSignal);
	}

	public void connectSlave(RtlMybusSlave slave) {
		slave.setStrobeSignal(strobeSignal);
		slave.setWriteEnableSignal(writeEnableSignal);
		slave.setAddressSignal(addressSignal);
		slave.setWriteDataSignal(writeDataSignal);
		readDataSignal.setConnected(slave.getReadDataSignal());
		ackSignal.setConnected(slave.getAckSignal());
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
