/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;

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
		RtlMemory ram = new RtlMemory(realm, 8 * 1024 * 1024, 32); // actually 16M x 16 DDR -> 8M x 32 SDR
		RtlSynchronousMemoryPort ramPort = ram.createSynchronousPort(clock,
			RtlSynchronousMemoryPort.ReadSupport.ASYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);

		// display LEDs
		new IntervalItem(controller, 10, 100_000_000, () -> { // 10 times per simulated second
			System.out.println(controller.getLeds().getValue());
		});

		// glue logic

		// 		ramPort.setAddressSignal(ramAddress.select(25, 0));
		ramPort.setAddressSignal(controller.getRamAddress().select(25, 0));
		ramPort.setWriteDataSignal(controller.getRamWriteData());
		ramPort.setClockEnableSignal(controller.getRamClockEnable());
		ramPort.setWriteEnableSignal(controller.getRamWriteEnable());
		controller.setRamReadData(ramPort.getReadDataSignal());

		// simulation
		new RtlClockGenerator(clock, 10); // 100 MHz (10 ns) clock
		controller.fire(controller::stopSimulation, 20_000_000_000L);
		controller.simulate();

	}

}
