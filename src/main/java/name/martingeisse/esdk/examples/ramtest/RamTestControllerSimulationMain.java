/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.model.validation.DesignValidationResult;
import name.martingeisse.esdk.core.model.validation.DesignValidator;
import name.martingeisse.esdk.core.model.validation.print.WriterValidationResultPrinter;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.library.mybus.rtl.RtlMybusOneToOneConnector;
import name.martingeisse.esdk.library.mybus.rtl.ram.RtlSimulatedDelayedMybusRam32;

/**
 * TODO make RamTestControllerBlockramSimulationMain work first -- it's simpler and with a small size should be synth'able.
 */
public class RamTestControllerSimulationMain {

	public static void main(String[] args) {

		// design
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		RamTestController controller = new RamTestController(realm, clock, new RtlBitConstant(realm, false));

		// RAM
		RtlSimulatedDelayedMybusRam32 ram = new RtlSimulatedDelayedMybusRam32(clock, 23, 3);
		RtlMybusOneToOneConnector mbConnector = new RtlMybusOneToOneConnector(realm);
		mbConnector.connectMaster(controller.getMybusMaster());
		mbConnector.connectSlave(ram);

		// display LEDs
		new IntervalItem(design, 10, 1_000_000, () -> { // 10 times per simulated second
			System.out.println(controller.getLeds().getValue());
		});

		// validate
		DesignValidationResult validationResult = new DesignValidator(design).validate();
		if (!validationResult.isValid(true)) {
			WriterValidationResultPrinter printer = new WriterValidationResultPrinter(System.out);
			validationResult.format(printer);
			printer.flush();
			return;
		}

		// simulation
		new RtlClockGenerator(clock, 10); // 100 MHz (10 ns) clock
		design.fire(design::stopSimulation, 20_000_000_000L);
		design.simulate();

	}

}
