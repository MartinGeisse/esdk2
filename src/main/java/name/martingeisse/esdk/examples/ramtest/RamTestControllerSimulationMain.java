/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

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
		RtlVectorSignal byteSelect = cpu.getPortAddress().select(1, 0);
		ramPort.setAddressSignal(ramAddress.select(25, 0));
		ramPort.setWriteDataSignal(ramWriteData);
		ramPort.setClockEnableSignal(cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)));
		ramPort.setWriteEnableSignal(cpu.getOutputData().select(0));
		ramReadData.setConnected(RtlBuilder.vectorRegister(clock, ramPort.getReadDataSignal(),
			cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)).and(cpu.getOutputData().select(0).not())));

		// simulation
		new RtlClockGenerator(clock, 10); // 100 MHz (10 ns) clock
		controller.fire(controller::stopSimulation, 20_000_000_000L);
		controller.simulate();

	}

}
