package name.martingeisse.esdk.examples.vga.test_renderer.display;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousRam;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public final class RtlMagicFramebufferDisplay extends RtlItem implements FramebufferDisplay {

	private final int widthBits;
	private final int heightBits;
	private final RtlSynchronousRam framebuffer;

	public RtlMagicFramebufferDisplay(RtlClockNetwork clockNetwork, int widthBits, int heightBits) {
		super(clockNetwork.getRealm());
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		// Note: rows and columns of the frame are not rows and columns of the RAM. Instead, the RAM
		// has one row per pixel and 3 columns (bits) for the 3 color channels.
		this.framebuffer = new RtlSynchronousRam(clockNetwork, 1 << (widthBits + heightBits), 3);
	}

	public void setWriteStrobeSignal(RtlBitSignal writeStrobeSignal) {
		framebuffer.setWriteEnableSignal(writeStrobeSignal);
	}

	public void setWriteAddressSignal(RtlVectorSignal writeAddressSignal) {
		framebuffer.setAddressSignal(writeAddressSignal);
	}

	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		framebuffer.setWriteDataSignal(writeDataSignal);
	}

	public RtlSynchronousRam getFramebuffer() {
		return framebuffer;
	}

}
