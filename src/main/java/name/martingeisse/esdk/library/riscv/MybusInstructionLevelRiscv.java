package name.martingeisse.esdk.library.riscv;

import name.martingeisse.esdk.library.mybus.transaction.TransactionMybus;

/**
 *
 */
public class MybusInstructionLevelRiscv extends InstructionLevelRiscv {

	private final TransactionMybus bus;

	public MybusInstructionLevelRiscv(TransactionMybus bus) {
		this.bus = bus;
	}

	@Override
	public int fetchInstruction(int wordAddress) {
		return bus.read(wordAddress);
	}

	@Override
	public int read(int wordAddress) {
		return bus.read(wordAddress);
	}

	@Override
	public void write(int wordAddress, int data, int byteMask) {
		bus.write(wordAddress, data, byteMask);
	}

}
