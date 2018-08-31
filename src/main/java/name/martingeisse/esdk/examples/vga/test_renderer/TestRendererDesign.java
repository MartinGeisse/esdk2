package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class TestRendererDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final FramebufferDisplay display;

	public TestRendererDesign(BufferedImage framebuffer, int widthBits) {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		display = new FramebufferDisplay(clock, framebuffer, widthBits);
		display.setWriteAddressSignal(new RtlVectorConstant(realm, VectorValue.ofUnsigned(14, 0)));
		display.setWriteStrobeSignal(new RtlBitConstant(realm, false));
		display.setWriteDataSignal(new RtlVectorConstant(realm, VectorValue.ofUnsigned(3, 4)));
	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public FramebufferDisplay getDisplay() {
		return display;
	}

}
