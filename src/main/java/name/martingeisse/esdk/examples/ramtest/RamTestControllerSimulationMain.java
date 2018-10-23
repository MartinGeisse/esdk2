/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.library.bus.wishbone.WishboneOneToOneConnector;
import name.martingeisse.esdk.library.bus.wishbone.ram.SimulatedDelayedWishboneRam32;

/**
 *
 */
public class RamTestControllerSimulationMain {

	public static void main(String[] args) {

		// design
		RamTestController controller = new RamTestController();
		RtlRealm realm = controller.getRealm();
		RtlClockNetwork clock = controller.getClock();

		// RAM
		SimulatedDelayedWishboneRam32 ram = new SimulatedDelayedWishboneRam32(clock, 23, 3);
		WishboneOneToOneConnector wbConnector = new WishboneOneToOneConnector(realm);
		wbConnector.connectMaster(controller.getWishboneMaster());
		wbConnector.connectSlave(ram);

		// display LEDs
		new IntervalItem(controller, 10, 100_000_000, () -> { // 10 times per simulated second
			System.out.println(controller.getLeds().getValue());
		});

		// simulation
		new RtlClockGenerator(clock, 10); // 100 MHz (10 ns) clock
		controller.fire(controller::stopSimulation, 20_000_000_000L);
		controller.simulate();

	}

}
