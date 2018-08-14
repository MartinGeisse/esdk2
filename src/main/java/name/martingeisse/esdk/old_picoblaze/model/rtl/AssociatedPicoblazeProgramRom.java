/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.old_picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousRom;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.old_picoblaze.model.instruction.AssociatedPicoblazeProgram;

/**
 * This is a synchronous ROM that loads and assembles a .psm from an associated resource.
 * <p>
 * Calling code must set the address signal using {@link #setAddressSignal(RtlVectorSignal)} and make use of the
 * instruction signal form {@link #getInstructionSignal()} as part of building the design
 */
public class AssociatedPicoblazeProgramRom extends RtlItem {

	private final RtlSynchronousRom rom;

	public AssociatedPicoblazeProgramRom(RtlRealm realm, RtlClockNetwork clockNetwork) {
		this(realm, clockNetwork, null, null);
	}

	public AssociatedPicoblazeProgramRom(RtlRealm realm, RtlClockNetwork clockNetwork, Class<?> anchorClass) {
		this(realm, clockNetwork, anchorClass, null);
	}

	public AssociatedPicoblazeProgramRom(RtlRealm realm, RtlClockNetwork clockNetwork, String suffix) {
		this(realm, clockNetwork, null, suffix);
	}

	public AssociatedPicoblazeProgramRom(RtlRealm realm, RtlClockNetwork clockNetwork, Class<?> anchorClass, String suffix) {
		super(realm);
		rom = new RtlSynchronousRom(clockNetwork, 1024, 18);
		if (anchorClass == null) {
			anchorClass = getClass();
		}
		AssociatedPicoblazeProgram program = new AssociatedPicoblazeProgram(anchorClass, suffix);
		for (int i = 0; i < 1024; i++) {
			rom.getMatrix().setRow(i, VectorValue.ofUnsigned(18, program.readInstruction(i)));
		}
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		rom.setAddressSignal(addressSignal);
	}

	public RtlVectorSignal getInstructionSignal() {
		return rom.getReadDataSignal();
	}

	public RtlSynchronousRom getRom() {
		return rom;
	}
}
