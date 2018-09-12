package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.examples.vga.test_renderer.display.FramebufferDisplay;

/**
 *
 */
public final class TestRendererDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final TestRenderer testRenderer;

	public TestRendererDesign(int widthBits, int heightBits) {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		testRenderer = new TestRenderer(getRealm(), clock, widthBits, heightBits);
	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public void connectDisplay(FramebufferDisplay display) {
		testRenderer.connectDisplay(display);
	}

}
