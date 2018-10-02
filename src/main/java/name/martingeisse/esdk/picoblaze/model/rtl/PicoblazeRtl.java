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
public class PicoblazeRtl extends RtlClockedItem.EmptySynthesis {

	private final PicoblazeState state;
	private boolean secondCycle;
	private final Kcpsm3ModuleInstance moduleInstance;
	private boolean sampledResetValue;
	private VectorValue sampledInstructionValue;
	private VectorValue sampledPortInputDataValue;

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
		this.secondCycle = true;
		this.moduleInstance = new Kcpsm3ModuleInstance(clockNetwork.getRealm());
		moduleInstance.getResetPort().setAssignedSignal(new RtlBitConstant(getRealm(), false));
		moduleInstance.getInstructionAddressPort().setSimulationSignal(RtlCustomVectorSignal.ofUnsigned(getRealm(), 10, state::getPc));

	}

	public void setResetSignal(RtlBitSignal resetSignal) {
		moduleInstance.getResetPort().setAssignedSignal(resetSignal);
	}

	public void setInstructionSignal(RtlVectorSignal instructionSignal) {
		moduleInstance.getInstructionPort().setAssignedSignal(instructionSignal);
	}

	public void setPortInputDataSignal(RtlVectorSignal portInputDataSignal) {
		moduleInstance.getDataInputPort().setAssignedSignal(portInputDataSignal);
	}

	public PicoblazeState getState() {
		return state;
	}

	// TODO RtlModuleInstanceOutputPort uses a SettableSignal but here we need a custom signal!

	public RtlVectorSignal getInstructionAddress() {
		return moduleInstance.getInstructionAddressPort().getSimulationSignal();
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
		if (moduleInstance.getResetPort().getAssignedSignal() == null) {
			throw new PicoblazeSimulatorException("no reset signal was set");
		}
		if (moduleInstance.getInstructionPort().getAssignedSignal() == null) {
			throw new PicoblazeSimulatorException("no instruction signal was set");
		}
		if (moduleInstance.getDataInputPort().getAssignedSignal() == null) {
			throw new PicoblazeSimulatorException("no port input data signal was set");
		}
	}

	@Override
	public void computeNextState() {
		sampledResetValue = moduleInstance.getResetPort().getAssignedSignal().getValue();
		sampledInstructionValue = moduleInstance.getInstructionPort().getAssignedSignal().getValue();
		sampledPortInputDataValue = moduleInstance.getDataInputPort().getAssignedSignal().getValue();
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
