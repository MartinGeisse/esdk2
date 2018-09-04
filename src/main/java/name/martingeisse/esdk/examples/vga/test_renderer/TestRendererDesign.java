package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class TestRendererDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final FramebufferDisplay display;
	private final TestRenderer testRenderer;

	public TestRendererDesign(BufferedImage framebuffer, int widthBits) {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		display = new FramebufferDisplay(clock, framebuffer, widthBits);
		testRenderer = new TestRenderer(clock);

		RtlVectorSignal rowRegister;
		{
			RtlBitSignal rowWriteEnable = testRenderer.getWriteStrobe().and(testRenderer.getPortAddress().select(7));
			RtlVectorSignal rowWriteData = testRenderer.getOutputData().select(6, 0);
			rowRegister = RtlBuilder.vectorRegister(clock, rowWriteData, rowWriteEnable, VectorValue.ofUnsigned(7, 0));
		}

		testRenderer.setPortInputDataSignal(new RtlVectorConstant(realm, VectorValue.ofUnsigned(8, 0)));
		testRenderer.setResetSignal(new RtlBitConstant(realm, false));
		display.setWriteAddressSignal(new RtlConcatenation(realm, rowRegister, testRenderer.getPortAddress().select(6, 0)));
		display.setWriteStrobeSignal(testRenderer.getWriteStrobe());
		display.setWriteDataSignal(testRenderer.getOutputData().select(2, 0));

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
