package name.martingeisse.esdk.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomBitSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.picoblaze.model.PicoblazeSimulatorException;
import name.martingeisse.esdk.picoblaze.model.PicoblazeState;

/**
 * An RTL Picoblaze model.
 * <p>
 * This model simulates the two-cycle behavior of the Picoblaze, including the I/O address being stable in the first
 * cycle. Use {@link #getPortAddress()} to obtain that address. Use {@link #getInstructionAddress()} to get the
 * instruction address as an RTL signal, but note that this signal is not correct during the first cycle, just like
 * for the real Picoblaze.
 */
public class PicoblazeRtl extends RtlClockedItem {

	private final PicoblazeState state;
	private RtlBitSignal resetSignal;
	private boolean sampledResetValue;
	private RtlVectorSignal instructionSignal;
	private VectorValue sampledInstructionValue;
	private RtlVectorSignal portInputDataSignal;
	private VectorValue sampledPortInputDataValue;
	private boolean secondCycle;

	public PicoblazeRtl(RtlClockNetwork clockNetwork) {
		super(clockNetwork);
		this.state = new PicoblazeState() {

			@Override
			protected int handleInput(int address) {
				return sampledPortInputDataValue.getAsUnsignedInt();
			}

			@Override
			protected void handleOutput(int address, int value) {
			}

		};
		this.resetSignal = new RtlBitConstant(getRealm(), false);
		this.secondCycle = true;
	}

	public void setResetSignal(RtlBitSignal resetSignal) {
		this.resetSignal = resetSignal;
	}

	public void setInstructionSignal(RtlVectorSignal instructionSignal) {
		this.instructionSignal = instructionSignal;
	}

	public void setPortInputDataSignal(RtlVectorSignal portInputDataSignal) {
		this.portInputDataSignal = portInputDataSignal;
	}

	public PicoblazeState getState() {
		return state;
	}

	public RtlVectorSignal getInstructionAddress() {
		return RtlCustomVectorSignal.ofUnsigned(getRealm(), 10, state::getPc);
	}

	public RtlVectorSignal getPortAddress() {
		return RtlCustomVectorSignal.ofUnsigned(getRealm(), 8, state::getPortAddress);
	}

	public RtlVectorSignal getOutputData() {
		return RtlCustomVectorSignal.ofUnsigned(getRealm(), 8, state::getPortOutputData);
	}

	public RtlBitSignal getReadStrobe() {
		return RtlCustomBitSignal.of(getRealm(), () -> secondCycle && state.isInputInstruction());
	}

	public RtlBitSignal getWriteStrobe() {
		return RtlCustomBitSignal.of(getRealm(), () -> secondCycle && state.isOutputInstruction());
	}

	@Override
	public void initializeSimulation() {
		if (resetSignal == null) {
			throw new PicoblazeSimulatorException("no reset signal was set");
		}
		if (instructionSignal == null) {
			throw new PicoblazeSimulatorException("no instruction signal was set");
		}
		if (portInputDataSignal == null) {
			throw new PicoblazeSimulatorException("no port input data signal was set");
		}
	}

	@Override
	public void computeNextState() {
		sampledResetValue = resetSignal.getValue();
		sampledInstructionValue = instructionSignal.getValue();
		sampledPortInputDataValue = portInputDataSignal.getValue();
	}

	@Override
	public void updateState() {
		if (sampledResetValue) {
			state.reset();
			// State state object will initialize the instruction to a NOP and here we jump right to execution
			// (second cycle). This will have no effect other than immediately loading the first instruction.
			secondCycle = true;
		} else if (secondCycle) {
			state.performSecondCycle();
			secondCycle = false;
		} else {
			state.setInstruction(sampledInstructionValue.getAsUnsignedInt());
			state.performFirstCycle();
			secondCycle = true;
		}
	}

}
