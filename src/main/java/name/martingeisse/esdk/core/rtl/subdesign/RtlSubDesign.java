package name.martingeisse.esdk.core.rtl.subdesign;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.block.RtlBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;

/**
 * This class is used as an adapter to use an {@link RtlDesign} as an {@link Item} in a larger high-level design.
 * This includes simulation of RTL subdesigns since there is currently no other way to perform RTL simulation.
 *
 * Note that only pin-less RTL subdesigns are currently supported. In the future, it might make sense to attach
 * simulation signals to {@link RtlPin} objects and thus simulate whole RTL designs.
 */
public final class RtlSubDesign extends Item {

	private final RtlDesign rtlDesign;

	public RtlSubDesign(Design design, RtlDesign rtlDesign) {
		super(design);
		this.rtlDesign = rtlDesign;
	}

	public RtlDesign getRtlDesign() {
		return rtlDesign;
	}

	@Override
	protected void initializeSimulation() {
		if (rtlDesign.getPins().iterator().hasNext()) {
			throw new IllegalStateException("cannot use an RtlDesign with pins in an RtlSubDesign");
		}

	}

	public void fireClockEdge(RtlClockNetwork clockNetwork) {
		fire(() -> onClockEdge(clockNetwork), 0);
	}

	private void onClockEdge(RtlClockNetwork clockNetwork) {
		for (RtlBlock block : rtlDesign.getBlocks()) {
			for (RtlProceduralSignal signal : block.getProceduralSignals()) {
				// TODO build a signal map
				// TODO initialize signal value
			}
			// TODO
		}
	}

}
