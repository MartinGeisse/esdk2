package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlMultiportMemory;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConditionalVectorOperation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 * Simple but wrong implementation that won't delay a write when reading pixels, instead writing correctly and
 * reading that pixel instead of the one that should actually be read.
 */
public final class RtlFramebufferDisplay extends RtlItem implements FramebufferDisplay {

	private final int widthBits;
	private final int heightBits;
	private final RtlMultiportMemory framebuffer;
	private final RtlSynchronousMemoryPort framebufferPort;
	private final RtlBitSignal readySignal;
	private RtlVectorSignal writeAddressSignal;
	private RtlVectorSignal dacAddressSignal;
	private RtlBitSignalConnector addressSelector;

	public RtlFramebufferDisplay(RtlClockNetwork clockNetwork, int widthBits, int heightBits) {
		super(clockNetwork.getRealm());
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		// Note: rows and columns of the frame are not rows and columns of the RAM. Instead, the RAM
		// has one row per pixel and 3 columns (bits) for the 3 color channels.
		this.framebuffer = new RtlMultiportMemory(getRealm(), 1 << (widthBits + heightBits), 3);
		this.framebufferPort = framebuffer.createSynchronousPort(clockNetwork,
			RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
		this.readySignal = new RtlBitConstant(clockNetwork.getRealm(), true);
		this.addressSelector = new RtlBitSignalConnector(getRealm());
	}

	public void setWriteStrobeSignal(RtlBitSignal writeStrobeSignal) {
		framebufferPort.setWriteEnableSignal(writeStrobeSignal);
		addressSelector.setConnected(writeStrobeSignal);
		updateFramebufferAddressSignal();
	}

	public void setWriteAddressSignal(RtlVectorSignal writeAddressSignal) {
		this.writeAddressSignal = writeAddressSignal;
		updateFramebufferAddressSignal();
	}

	public void setDacAddressSignal(RtlVectorSignal dacAddressSignal) {
		this.dacAddressSignal = dacAddressSignal;
		updateFramebufferAddressSignal();
	}

	private void updateFramebufferAddressSignal() {
		if (writeAddressSignal != null) {
			if (dacAddressSignal != null) {
				framebufferPort.setAddressSignal(new RtlConditionalVectorOperation(getRealm(),
					addressSelector, writeAddressSignal, dacAddressSignal));
			} else {
				framebufferPort.setAddressSignal(writeAddressSignal);
			}
		} else {
			if (dacAddressSignal != null) {
				framebufferPort.setAddressSignal(dacAddressSignal);
			}
		}
	}

	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		framebufferPort.setWriteDataSignal(writeDataSignal);
	}

	@Override
	public RtlBitSignal getReadySignal() {
		return readySignal;
	}

	public RtlMultiportMemory getFramebuffer() {
		return framebuffer;
	}

	public RtlVectorSignal getDacReadDataSignal() {
		return framebufferPort.getReadDataSignal();
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
