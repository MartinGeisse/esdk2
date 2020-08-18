package name.martingeisse.esdk.library.picoblaze.model.instruction;

/**
 *
 */
public final class PicoblazeProgramHandlerFixed implements PicoblazeProgramHandler {

	private final int[] program;

	public PicoblazeProgramHandlerFixed(int[] program) {
		if (program.length != 1024) {
			throw new IllegalArgumentException("program length must be 1024, is " + program.length);
		}
		this.program = program;
	}

	@Override
	public int readInstruction(int address) {
		return program[address & 1023];
	}

}
