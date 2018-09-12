package name.martingeisse.esdk.examples.vga.test_renderer.display;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousRam;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 * Simple but wrong implementation that won't delay a write when reading pixels, instead writing correctly and
 * reading that pixel instead of the one that should actually be read.
 */
public final class RtlFramebufferDisplay extends RtlItem implements FramebufferDisplay {

	private final int widthBits;
	private final int heightBits;
	private final RtlSynchronousRam framebuffer;
	private final RtlBitSignal readySignal;

	public RtlFramebufferDisplay(RtlClockNetwork clockNetwork, int widthBits, int heightBits) {
		super(clockNetwork.getRealm());
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		// Note: rows and columns of the frame are not rows and columns of the RAM. Instead, the RAM
		// has one row per pixel and 3 columns (bits) for the 3 color channels.
		this.framebuffer = new RtlSynchronousRam(clockNetwork, 1 << (widthBits + heightBits), 3);
		this.readySignal = new RtlBitConstant(clockNetwork.getRealm(), true);
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

	@Override
	public RtlBitSignal getReadySignal() {
		return readySignal;
	}

	public RtlSynchronousRam getFramebuffer() {
		return framebuffer;
	}

}
