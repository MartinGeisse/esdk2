/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.old_picoblaze.model.rtl;

import name.martingeisse.esdk.old_picoblaze.model.instruction.AssociatedPicoblazeProgram;

/**
 * TODO should implement, at RTL level, a synchronous ROM that loads and assembles a .psm file using
 * {@link AssociatedPicoblazeProgram} but uses the subclass of this class as the default.
 */
public class AssociatedPicoblazeProgramRom {

	public AssociatedPicoblazeProgramRom() {
		this(null, null);
	}

	public AssociatedPicoblazeProgramRom(Class<?> anchorClass) {
		this(anchorClass, null);
	}

	public AssociatedPicoblazeProgramRom(String suffix) {
		this(null, suffix);
	}

	public AssociatedPicoblazeProgramRom(Class<?> anchorClass, String suffix) {
		if (anchorClass == null) {
			anchorClass = getClass();
		}
		AssociatedPicoblazeProgram program = new AssociatedPicoblazeProgram(anchorClass, suffix);
		for (int i = 0; i < 1024; i++) {
			int instruction = program.readInstruction(i);
		}
		// TODO
	}

}
