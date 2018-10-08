package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 * A write transaction occurs when the ready and write-strobe signals are active at the same time. Any other
 * combination means that no write transaction occurs.
 */
public interface FramebufferDisplay {

	RtlBitSignal getReadySignal();
	void setWriteStrobeSignal(RtlBitSignal writeStrobeSignal);
	void setWriteAddressSignal(RtlVectorSignal writeAddressSignal);
	void setWriteDataSignal(RtlVectorSignal writeDataSignal);

}
