package name.martingeisse.esdk.riscv.instruction.io;

/**
 *
 */
public interface IoUnit {

	int fetchInstruction(int wordAddress);

	int read(int wordAddress);

	void write(int wordAddress, int data, int byteMask);

}
