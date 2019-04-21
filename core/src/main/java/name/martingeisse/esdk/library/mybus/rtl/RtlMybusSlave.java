package name.martingeisse.esdk.library.mybus.rtl;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public interface RtlMybusSlave {

	void setStrobeSignal(RtlBitSignal strobeSignal);

	void setWriteEnableSignal(RtlBitSignal writeEnableSignal);

	void setAddressSignal(RtlVectorSignal addressSignal);

	void setWriteDataSignal(RtlVectorSignal writeDataSignal);

	RtlVectorSignal getReadDataSignal();

	RtlBitSignal getAckSignal();

}
