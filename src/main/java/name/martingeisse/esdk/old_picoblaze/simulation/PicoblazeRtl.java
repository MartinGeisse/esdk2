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
		return new RtlCustomVectorSignal(getRealm()) {

			@Override
			public int getWidth() {
				return 10;
			}

			@Override
			public VectorValue getValue() {
				// TODO does this reflect the correct value both for increments and jumps?
				return VectorValue.ofUnsigned(10, state.getPc());
			}

		};
	}

	public RtlVectorSignal getIoAddress() {
		return new RtlCustomVectorSignal(getRealm()) {

			@Override
			public int getWidth() {
				return 8;
			}

			@Override
			public VectorValue getValue() {
				// TODO does this reflect the correct value both for immediate and register addresses?
				return VectorValue.ofUnsigned(8, 0 /*TODO*/);
			}

		};
	}

	public RtlVectorSignal getIoData() {
		return new RtlCustomVectorSignal(getRealm()) {

			@Override
			public int getWidth() {
				return 8;
			}

			@Override
			public VectorValue getValue() {
				return VectorValue.ofUnsigned(8, 0 /*TODO*/);
			}

		};
	}

	public RtlBitSignal getReadStrobe() {
		return new RtlCustomBitSignal(getRealm()) {

			@Override
			public boolean getValue() {
				return false; // TODO
			}

		};
	}

	public RtlBitSignal getWriteStrobe() {
		return new RtlCustomBitSignal(getRealm()) {

			@Override
			public boolean getValue() {
				return false; // TODO
			}

		};
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
