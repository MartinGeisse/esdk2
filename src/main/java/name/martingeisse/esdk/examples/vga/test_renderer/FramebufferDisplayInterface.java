package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public interface FramebufferDisplayInterface {

	void setWriteStrobeSignal(RtlBitSignal writeStrobeSignal);
	void setWriteAddressSignal(RtlVectorSignal writeAddressSignal);
	void setWriteDataSignal(RtlVectorSignal writeDataSignal);

}
