package name.martingeisse.esdk.library.bus.mybus;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public interface MybusMaster {

	RtlBitSignal getStrobeSignal();

	RtlBitSignal getWriteEnableSignal();

	RtlVectorSignal getAddressSignal();

	RtlVectorSignal getWriteDataSignal();

	void setReadDataSignal(RtlVectorSignal readDataSignal);

	void setAckSignal(RtlBitSignal ackSignal);

}
