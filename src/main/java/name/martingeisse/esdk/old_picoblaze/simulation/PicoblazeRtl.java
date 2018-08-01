package name.martingeisse.esdk.old_picoblaze.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.old_picoblaze.simulation.port.PicoblazePortHandler;
import name.martingeisse.esdk.old_picoblaze.simulation.port.PicoblazePortHandlerRtl;
import name.martingeisse.esdk.old_picoblaze.simulation.program.PicoblazeProgramHandler;
import name.martingeisse.esdk.old_picoblaze.simulation.program.PicoblazeProgramHandlerRtl;

/**
 * An RTL-compatible Picoblaze model.
 * <p>
 * This model simulates the two-cycle behavior of the Picoblaze, including the I/O address being stable in the first
 * cycle. Use {@link #getIoAddress()} to obtain that address. Use {@link #getInstructionAddress()} to get the
 * instruction address as an RTL signal, but note that this signal is not correct during the first cycle.
 * <p>
 * The actual I/O behavior can still be configured using a {@link PicoblazePortHandler}. To build a pure RTL model,
 * use a {@link PicoblazePortHandlerRtl}.
 * <p>
 * Similarly, the program can be configured using a {@link PicoblazeProgramHandler}. For a pure RTL model, use
 * a {@link PicoblazeProgramHandlerRtl}.
 */
public class PicoblazeRtl extends RtlClockedItem {

	private final PicoblazeState state;
	private boolean secondCycle;
	private int instruction;

	public PicoblazeRtl(RtlRealm realm, RtlClockNetwork clockNetwork) {
		super(realm, clockNetwork);
		this.state = new PicoblazeState();

		// Initialize the instruction to a NOP and jump right to execution (second cycle). This will have no effect
		// other than immediately loading the first instruction.
		this.secondCycle = true;
		this.instruction = 0x01000; // LOAD s0, s0
	}

	public RtlVectorSignal getInstructionAddress() {
		// TODO
	}

	public RtlVectorSignal getIoAddress() {
		// TODO
	}

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
		// TODO
	}

	@Override
	public void updateState() {
		// TODO
	}

}
