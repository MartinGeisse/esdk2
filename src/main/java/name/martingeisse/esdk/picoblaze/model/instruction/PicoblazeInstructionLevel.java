package name.martingeisse.esdk.picoblaze.model.instruction;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.picoblaze.model.PicoblazeSimulatorException;
import name.martingeisse.esdk.picoblaze.model.PicoblazeState;

/**
 * An instruction-level Picoblaze model.
 */
public class PicoblazeInstructionLevel extends Item {

	private final PicoblazeProgramHandler programHandler;
	private final PicoblazePortHandler portHandler;
	private final PicoblazeState state;

	public PicoblazeInstructionLevel(Design design, PicoblazeProgramHandler programHandler, PicoblazePortHandler portHandler) {
		super(design);
		if (programHandler == null) {
			throw new PicoblazeSimulatorException("programHandler cannot be null");
		}
		if (portHandler == null) {
			throw new PicoblazeSimulatorException("portHandler cannot be null");
		}
		this.programHandler = programHandler;
		this.portHandler = portHandler;
		this.state = new PicoblazeState() {

			@Override
			protected int handleInput(int address) {
				return PicoblazeInstructionLevel.this.portHandler.handleInput(address);
			}

			@Override
			protected void handleOutput(int address, int value) {
				PicoblazeInstructionLevel.this.portHandler.handleOutput(address, value);
			}

		};
	}

	public PicoblazeState getState() {
		return state;
	}

	public void step() {
		state.setInstruction(programHandler.readInstruction(state.getPc()));
		state.performFirstCycle();
		state.performSecondCycle();
	}

}
