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
 *
 * TODO: asynchronous blocks cache their results implicitly, inside their RtlProceduralSignal objects.
 * They have to be updated immediately when their inputs change (Verilog: always @(*) does that).
 * But we can't right now. Solutions:
 * - allow to register listeners to RtlSignal. Cumbersome for custom signal writers and we have to
 *   implement some kind of "grouping" to avoid registering lots of event callbacks
 * - don't allow custom RtlSignal implementations. Other models must set the value explicitly, and this
 *   calls listeners / block execution.
 * - additionally allow custom RtlSignal implementations that must support listeners. No advantages over
 *   calling a setter -- code is still as complex and it's even more error-prone.
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
