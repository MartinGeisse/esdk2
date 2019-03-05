package name.martingeisse.esdk.library.bus.mybus;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public interface MybusSlave {

	void setStrobeSignal(RtlBitSignal strobeSignal);

	void setWriteEnableSignal(RtlBitSignal writeEnableSignal);

	void setAddressSignal(RtlVectorSignal addressSignal);

	void setWriteDataSignal(RtlVectorSignal writeDataSignal);

	RtlVectorSignal getReadDataSignal();

	RtlBitSignal getAckSignal();

}
