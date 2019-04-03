package name.martingeisse.esdk.library.riscv.io;

/**
 *
 */
public interface IoUnit {

	int fetchInstruction(int wordAddress);

	int read(int wordAddress);

	void write(int wordAddress, int data, int byteMask);

}
