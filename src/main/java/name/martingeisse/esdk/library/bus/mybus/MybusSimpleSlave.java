package name.martingeisse.esdk.library.bus.mybus;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 * Non-pipelined, single-transfer cycle slave.
 *
 * For single-transfer bus cycles without extra delaying clock cycles, the CYC and STB signals are identical and are
 * called "cycle strobe" here.
 */
public interface MybusSimpleSlave {

	void setCycleStrobeSignal(RtlBitSignal cycleStrobeSignal);

	void setWriteEnableSignal(RtlBitSignal writeEnableSignal);

	void setAddressSignal(RtlVectorSignal addressSignal);

	void setWriteDataSignal(RtlVectorSignal writeDataSignal);

	RtlVectorSignal getReadDataSignal();

	RtlBitSignal getAckSignal();

}
