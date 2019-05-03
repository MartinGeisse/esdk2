package name.martingeisse.esdk.riscv.simulator.io;

import name.martingeisse.esdk.library.mybus.transaction.TransactionMybus;

/**
 *
 */
public final class MybusIoUnit implements IoUnit {

	private final TransactionMybus bus;

	public MybusIoUnit(TransactionMybus bus) {
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
