package name.martingeisse.esdk.library.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.module.*;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedComputedBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedComputedVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.picoblaze.model.PicoblazeSimulatorException;
import name.martingeisse.esdk.library.picoblaze.model.PicoblazeState;

/**
 * An RTL Picoblaze model.
 * <p>
 * This model simulates the two-cycle behavior of the Picoblaze, including the I/O address being stable in the first
 * cycle. Use {@link #getPortAddress()} to obtain that address. Use {@link #getInstructionAddress()} to get the
 * instruction address as an RTL signal, but note that this signal is not correct during the first cycle, just like
 * for the real Picoblaze.
 * <p>
 * TODO: interrupts
 */
public class PicoblazeRtl extends RtlClockedItem {

	private final PicoblazeState state;
	private boolean secondCycle;

	private final RtlModuleInstance moduleInstance;
	private final RtlInstanceBitInputPort clockPort;
	private final RtlInstanceBitInputPort resetPort;
	private final RtlInstanceVectorOutputPort instructionAddressPort;
	private final RtlInstanceVectorInputPort instructionPort;
	private final RtlInstanceBitOutputPort readStrobePort;
	private final RtlInstanceBitOutputPort writeStrobePort;
	private final RtlInstanceVectorOutputPort portIdPort;
	private final RtlInstanceVectorInputPort dataInputPort;
	private final RtlInstanceVectorOutputPort dataOutputPort;
	private final RtlInstanceBitInputPort interruptPort;
	private final RtlInstanceBitOutputPort interruptAckPort;

	private boolean sampledResetValue;
	private VectorValue sampledInstructionValue;
	private VectorValue sampledPortInputDataValue;

	private boolean delayInstructionStabilityCheck;

	public PicoblazeRtl(RtlClockNetwork clockNetwork) {
		super(clockNetwork);
		state = new PicoblazeState() {

			@Override
			protected int handleInput(int address) {
				// TODO this is probably also wrong WRT exact timing
//				if ((address & 64) != 0) {
					// System.out.println("in: " + address + ": " + sampledPortInputDataValue.getAsUnsignedInt());
//				}
				return sampledPortInputDataValue.getAsUnsignedInt();
			}

			@Override
			protected void handleOutput(int address, int value) {
//				if ((address & 32) != 0) {
					// System.out.println("out: " + address + ": " + value);
//				}
			}

		};
		secondCycle = true;
		delayInstructionStabilityCheck = true;

		moduleInstance = new RtlModuleInstance(clockNetwork.getRealm(), "kcpsm3");
		clockPort = moduleInstance.createBitInputPort("clk");
		clockPort.setAssignedSignal(clockNetwork.getClockSignal());
		resetPort = moduleInstance.createBitInputPort("reset");
		resetPort.setAssignedSignal(new RtlBitConstant(getRealm(), false));
		instructionAddressPort = moduleInstance.createVectorOutputPort("address", 10);
		instructionAddressPort.setSimulationSignal(RtlSimulatedComputedVectorSignal.of(getRealm(), 10, state::getPc));
		instructionPort = moduleInstance.createVectorInputPort("instruction", 18);
		readStrobePort = moduleInstance.createBitOutputPort("read_strobe");
		readStrobePort.setSimulationSignal(RtlSimulatedComputedBitSignal.of(getRealm(), () -> secondCycle && state.isInputInstruction()));
		writeStrobePort = moduleInstance.createBitOutputPort("write_strobe");
		writeStrobePort.setSimulationSignal(RtlSimulatedComputedBitSignal.of(getRealm(), () -> secondCycle && state.isOutputInstruction()));
		portIdPort = moduleInstance.createVectorOutputPort("port_id", 8);
		portIdPort.setSimulationSignal(RtlSimulatedComputedVectorSignal.of(getRealm(), 8, state::getPortAddress));
		dataInputPort = moduleInstance.createVectorInputPort("in_port", 8);
		dataOutputPort = moduleInstance.createVectorOutputPort("out_port", 8);
		dataOutputPort.setSimulationSignal(RtlSimulatedComputedVectorSignal.of(getRealm(), 8, state::getPortOutputData));
		interruptPort = moduleInstance.createBitInputPort("interrupt");
		interruptPort.setAssignedSignal(new RtlBitConstant(getRealm(), false));
		interruptAckPort = moduleInstance.createBitOutputPort("interrupt_ack");
		interruptAckPort.setSimulationSignal(new RtlBitConstant(getRealm(), false));
	}

	public void setResetSignal(RtlBitSignal resetSignal) {
		resetPort.setAssignedSignal(resetSignal);
	}

	public void setInstructionSignal(RtlVectorSignal instructionSignal) {
		instructionPort.setAssignedSignal(instructionSignal);
	}

	public void setPortInputDataSignal(RtlVectorSignal portInputDataSignal) {
		dataInputPort.setAssignedSignal(portInputDataSignal);
	}

	public void setInterruptSignal(RtlBitSignal resetSignal) {
		interruptPort.setAssignedSignal(resetSignal);
	}

	public PicoblazeState getState() {
		return state;
	}

	public RtlVectorSignal getInstructionAddress() {
		return instructionAddressPort;
	}

	public RtlVectorSignal getPortAddress() {
		return portIdPort;
	}

	public RtlVectorSignal getOutputData() {
		return dataOutputPort;
	}

	public RtlBitSignal getReadStrobe() {
		return readStrobePort;
	}

	public RtlBitSignal getWriteStrobe() {
		return writeStrobePort;
	}

	@Override
	public void initializeSimulation() {
		if (resetPort.getAssignedSignal() == null) {
			throw new PicoblazeSimulatorException("no reset signal was set");
		}
		if (instructionPort.getAssignedSignal() == null) {
			throw new PicoblazeSimulatorException("no instruction signal was set");
		}
		if (dataInputPort.getAssignedSignal() == null) {
			throw new PicoblazeSimulatorException("no port input data signal was set");
		}
	}

	@Override
	public void computeNextState() {
		sampledResetValue = resetPort.getAssignedSignal().getValue();
		if (sampledResetValue || delayInstructionStabilityCheck || !secondCycle) {
			state.setInstruction(instructionPort.getAssignedSignal().getValue().getAsUnsignedInt());
		} else {
			if (state.getInstruction() != instructionPort.getAssignedSignal().getValue().getAsUnsignedInt()) {
				throw new RuntimeException("Picoblaze instruction changed from second to first cycle");
			}
		}
		sampledPortInputDataValue = dataInputPort.getAssignedSignal().getValue();
	}

	@Override
	public void updateState() {
		if (sampledResetValue) {
			state.reset();
			// the state object will initialize the instruction to a NOP and here we jump right to execution
			// (second cycle). This will have no effect other than immediately loading the first instruction.
			secondCycle = true;
			delayInstructionStabilityCheck = true;
		} else if (secondCycle) {
			state.performSecondCycle();
			secondCycle = false;
			delayInstructionStabilityCheck = false;
		} else {
			state.performFirstCycle();
			secondCycle = true;
			delayInstructionStabilityCheck = false;
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
