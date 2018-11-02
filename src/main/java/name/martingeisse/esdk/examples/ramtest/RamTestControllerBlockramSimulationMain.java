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
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitSignal;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlMemoryPort;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.library.bus.wishbone.WishboneOneToOneConnector;
import name.martingeisse.esdk.library.bus.wishbone.ram.SimulatedDelayedWishboneRam32;

/**
 *
 */
public class RamTestControllerBlockramSimulationMain {

	public static void main(String[] args) {

		// design
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		RamTestController controller = new RamTestController(realm, clock, new RtlBitConstant(realm, false));

		// BlockRAM test
		RtlClockedBlock block = new RtlClockedBlock(clock);
		RtlProceduralBitSignal ackReg = block.createBit(false);
		block.getStatements().when(controller.getWishboneMaster().getCycleStrobeSignal()).getThenBranch()
			.assign(ackReg, ackReg.not());
		controller.getWishboneMaster().setAckSignal(ackReg);
		RtlMemory ram = new RtlMemory(realm, 256, 32);
		RtlSynchronousMemoryPort ramPort = ram.createSynchronousPort(clock,
			RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
		ramPort.setClockEnableSignal(controller.getWishboneMaster().getCycleStrobeSignal());
		ramPort.setWriteEnableSignal(controller.getWishboneMaster().getWriteEnableSignal());
		ramPort.setAddressSignal(controller.getWishboneMaster().getAddressSignal().select(7, 0));
		ramPort.setWriteDataSignal(controller.getWishboneMaster().getWriteDataSignal());
		controller.getWishboneMaster().setReadDataSignal(ramPort.getReadDataSignal());

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