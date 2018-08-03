package name.martingeisse.esdk.old_picoblaze.model.instruction;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.old_picoblaze.model.PicoblazeState;

/**
 * An instruction-level Picoblaze model.
 */
public class PicoblazeInstructionLevel extends Item {

	private final PicoblazeProgramHandler programHandler;
	private final PicoblazePortHandler portHandler;
	private final PicoblazeState state;

	public PicoblazeInstructionLevel(Design design, PicoblazeProgramHandler programHandler, PicoblazePortHandler portHandler) {
		super(design);
		this.programHandler = programHandler;
		this.portHandler = portHandler;
		this.state = new PicoblazeState();
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
