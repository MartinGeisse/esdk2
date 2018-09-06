package name.martingeisse.esdk.examples.vga.test_renderer.small;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.examples.vga.test_renderer.display.SimulatedFramebufferDisplay;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class TestRendererDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final SimulatedFramebufferDisplay display;
	private final PicoblazeRtlWithAssociatedProgram cpu;

	public TestRendererDesign(BufferedImage framebuffer, int widthBits) {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		display = new SimulatedFramebufferDisplay(clock, framebuffer, widthBits);
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, getClass());

		RtlVectorSignal rowRegister;
		{
			RtlBitSignal rowWriteEnable = cpu.getWriteStrobe().and(cpu.getPortAddress().select(7));
			RtlVectorSignal rowWriteData = cpu.getOutputData().select(6, 0);
			rowRegister = RtlBuilder.vectorRegister(clock, rowWriteData, rowWriteEnable, VectorValue.ofUnsigned(7, 0));
		}

		cpu.setPortInputDataSignal(new RtlVectorConstant(realm, VectorValue.ofUnsigned(8, 0)));
		cpu.setResetSignal(new RtlBitConstant(realm, false));
		display.setWriteAddressSignal(new RtlConcatenation(realm, rowRegister, cpu.getPortAddress().select(6, 0)));
		display.setWriteStrobeSignal(cpu.getWriteStrobe());
		display.setWriteDataSignal(cpu.getOutputData().select(2, 0));

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public SimulatedFramebufferDisplay getDisplay() {
		return display;
	}

}
