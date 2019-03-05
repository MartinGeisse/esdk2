package name.martingeisse.esdk.library.bus.mybus;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 * Non-pipelined, single-transfer cycle master.
 *
 * For single-transfer bus cycles without extra delaying clock cycles, the CYC and STB signals are identical and are
 * called "cycle strobe" here.
 */
public interface MybusSimpleMaster {

	RtlBitSignal getCycleStrobeSignal();

	RtlBitSignal getWriteEnableSignal();

	RtlVectorSignal getAddressSignal();

	RtlVectorSignal getWriteDataSignal();

	void setReadDataSignal(RtlVectorSignal readDataSignal);

	void setAckSignal(RtlBitSignal ackSignal);

}
