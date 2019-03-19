package name.martingeisse.esdk.library.mybus.rtl;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public interface RtlMybusMaster {

	RtlBitSignal getStrobeSignal();

	RtlBitSignal getWriteEnableSignal();

	RtlVectorSignal getAddressSignal();

	RtlVectorSignal getWriteDataSignal();

	void setReadDataSignal(RtlVectorSignal readDataSignal);

	void setAckSignal(RtlBitSignal ackSignal);

}
