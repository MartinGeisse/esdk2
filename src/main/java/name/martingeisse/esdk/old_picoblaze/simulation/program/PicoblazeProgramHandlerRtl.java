package name.martingeisse.esdk.old_picoblaze.simulation.program;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public final class PicoblazeProgramHandlerRtl implements PicoblazeProgramHandler {

	private final RtlVectorSignal instruction;

	public PicoblazeProgramHandlerRtl(RtlVectorSignal instruction) {
		if (instruction.getWidth() != 18) {
			throw new IllegalArgumentException("instruction width must be 18, is " + instruction.getWidth());
		}
		this.instruction = instruction;
	}

	@Override
	public int readInstruction(int address) {
		return instruction.getValue().getAsUnsignedInt();
	}

}
