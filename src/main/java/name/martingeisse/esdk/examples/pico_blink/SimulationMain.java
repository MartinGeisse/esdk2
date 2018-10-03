/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.pico_blink;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
		PicoBlinkDesign design = new PicoBlinkDesign();
		new RtlClockGenerator(design.getClock(), 20);
		design.fire(design::stopSimulation, 200_000_000);
		new IntervalItem(design, 10, 2_000_000, () -> {
			System.out.println(design.getLeds().getValue());
		});
		design.simulate();
	}

}
