package name.martingeisse.esdk.library.riscv;

/**
 *
 */
public final class InstructionEncoder {

	// prevent instantiation
	private InstructionEncoder() {
	}

	public static int opImmediate(int funct3, int destination, int source1, int immediate) {
		if (funct3 < 0 || funct3 > 7) {
			throw new IllegalArgumentException("invalid funct3: " + funct3);
		}
		if (destination < 0 || destination > 31) {
			throw new IllegalArgumentException("invalid destination: " + destination);
		}
		if (source1 < 0 || source1 > 31) {
			throw new IllegalArgumentException("invalid source1: " + source1);
		}
		if (immediate < -2048 || immediate > 2047) {
			throw new IllegalArgumentException("invalid immediate: " + immediate);
		}
		return (immediate << 20) | (source1 << 15) | (funct3 << 12) | (destination << 7) | 0x13;
	}

	public static int addi(int destination, int source1, int immediate) {
		return opImmediate(0, destination, source1, immediate);
	}

	public static int subi(int destination, int source1, int immediate) {
		return addi(destination, source1, -immediate);
	}

	public static int nop() {
		return addi(0, 0, 0);
	}

	public static int slti(int destination, int source1, int immediate) {
		return opImmediate(2, destination, source1, immediate);
	}

	public static int sltiu(int destination, int source1, int immediate) {
		return opImmediate(3, destination, source1, immediate);
	}

	public static int xori(int destination, int source1, int immediate) {
		return opImmediate(4, destination, source1, immediate);
	}

	public static int ori(int destination, int source1, int immediate) {
		return opImmediate(6, destination, source1, immediate);
	}

	public static int andi(int destination, int source1, int immediate) {
		return opImmediate(7, destination, source1, immediate);
	}

}
