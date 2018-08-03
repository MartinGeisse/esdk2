package name.martingeisse.esdk.old_picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomBitSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.old_picoblaze.model.PicoblazeState;

/**
 * An RTL Picoblaze model.
 * <p>
 * This model simulates the two-cycle behavior of the Picoblaze, including the I/O address being stable in the first
 * cycle. Use {@link #getIoAddress()} to obtain that address. Use {@link #getInstructionAddress()} to get the
 * instruction address as an RTL signal, but note that this signal is not correct during the first cycle, just like
 * for the real Picoblaze.
 */
public class PicoblazeRtl extends RtlClockedItem {

	private final PicoblazeState state;

	public PicoblazeRtl(RtlRealm realm, RtlClockNetwork clockNetwork) {
		super(realm, clockNetwork);
		this.state = new PicoblazeState();

		// TODO no program handler or port handler for an RTL model!

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
		// Problem: The PicoblazeState doesn't conform to RtlClockedItem -- it reads external state and changes
		// internal state in the same method call. It even changes external state through the port handler in the
		// same call.
	}

	@Override
	public void updateState() {
		// TODO
	}

}
