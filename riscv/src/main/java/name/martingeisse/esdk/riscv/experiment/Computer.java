package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;

/**
 *
 */
public class Computer extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final ComputerModule computerModule;

	public Computer() {
		this.realm = new RtlRealm(this);
		this.clock = realm.createClockNetwork();
		this.computerModule = new ComputerModule(realm, clock);
	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public ComputerModule getComputerModule() {
		return computerModule;
	}

}
