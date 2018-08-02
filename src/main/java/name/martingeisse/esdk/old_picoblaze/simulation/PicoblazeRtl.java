package name.martingeisse.esdk.old_picoblaze.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomBitSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;
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
 * <p>
 * This model supports resetting the Picoblaze either through an RTL signal or by calling a method. Both ways can be
 * mixed; however, if the reset method gets called from within {@link RtlClockedItem#updateState()}, then it is
 * undefined whether the Picoblaze executes the current cycle before or after the reset.
 */
public class PicoblazeRtl extends RtlClockedItem {

	private final PicoblazeState state;

	public PicoblazeRtl(RtlRealm realm, RtlClockNetwork clockNetwork) {
		super(realm, clockNetwork);
		this.state = new PicoblazeState();
	}

	public PicoblazeState getState() {
		return state;
	}

	public RtlVectorSignal getInstructionAddress() {
		return RtlCustomVectorSignal.ofUnsigned(getRealm(), 10, state::getPc);
	}

	public RtlVectorSignal getIoAddress() {
		return RtlCustomVectorSignal.ofUnsigned(getRealm(), 10, state::getPortAddress);
	}

	public RtlVectorSignal getIoData() {
		return RtlCustomVectorSignal.ofUnsigned(getRealm(), 10, state::getPortOutputData);
	}

	public RtlBitSignal getReadStrobe() {
		return RtlCustomBitSignal.of(getRealm(), state::getReadStrobe);
	}

	public RtlBitSignal getWriteStrobe() {
		return RtlCustomBitSignal.of(getRealm(), state::getWriteStrobe);
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
