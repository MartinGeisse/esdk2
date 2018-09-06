package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class FramebufferDisplay extends RtlClockedItem implements FramebufferDisplayInterface {

	private final BufferedImage framebuffer;
	private final int widthBits;
	private final int xMask;

	private RtlBitSignal writeStrobeSignal;
	private RtlVectorSignal writeAddressSignal;
	private RtlVectorSignal writeDataSignal;

	private boolean writeStrobe;
	private int writeAddress;
	private int writeData;

	public FramebufferDisplay(RtlClockNetwork clockNetwork, BufferedImage framebuffer, int widthBits) {
		super(clockNetwork);
		this.framebuffer = framebuffer;
		this.widthBits = widthBits;
		this.xMask = (1 << widthBits) - 1;
	}

	public BufferedImage getFramebuffer() {
		return framebuffer;
	}

	public RtlBitSignal getWriteStrobeSignal() {
		return writeStrobeSignal;
	}

	public void setWriteStrobeSignal(RtlBitSignal writeStrobeSignal) {
		this.writeStrobeSignal = writeStrobeSignal;
	}

	public RtlVectorSignal getWriteAddressSignal() {
		return writeAddressSignal;
	}

	public void setWriteAddressSignal(RtlVectorSignal writeAddressSignal) {
		this.writeAddressSignal = writeAddressSignal;
	}

	public RtlVectorSignal getWriteDataSignal() {
		return writeDataSignal;
	}

	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		if (writeDataSignal.getWidth() != 3) {
			throw new IllegalArgumentException("write data width must be 3, is " + writeDataSignal.getWidth());
		}
		this.writeDataSignal = writeDataSignal;
	}

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
		writeStrobe = writeStrobeSignal.getValue();
		writeAddress = writeAddressSignal.getValue().getAsUnsignedInt();
		writeData = writeDataSignal.getValue().getAsUnsignedInt();
	}

	@Override
	public void updateState() {
		if (writeStrobe) {
			int x = writeAddress & xMask;
			int y = writeAddress >>> widthBits;
			if (x >= 0 && x < framebuffer.getWidth() && y >= 0 && y < framebuffer.getWidth()) {
				int rgb = (writeData & 4) != 0 ? 0xff0000 : 0;
				rgb |= (writeData & 2) != 0 ? 0xff00 : 0;
				rgb |= (writeData & 1) != 0 ? 0xff : 0;
				framebuffer.setRGB(x, y, rgb);
			}
		}
	}

}
