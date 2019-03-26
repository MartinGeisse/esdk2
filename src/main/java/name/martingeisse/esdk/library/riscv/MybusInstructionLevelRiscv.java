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
	protected int fetchInstruction(int wordAddress) {
		return bus.read(wordAddress);
	}

	@Override
	protected int read(int wordAddress) {
		return bus.read(wordAddress);
	}

	@Override
	protected void write(int wordAddress, int data, int byteMask) {
		bus.write(wordAddress, data, byteMask);
	}

}
