/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousRom;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.picoblaze.model.ProgramRomUtil;

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

	public AssociatedPicoblazeProgramRom(RtlRealm realm, RtlClockNetwork clockNetwork, String programSuffix) {
		this(realm, clockNetwork, null, programSuffix);
	}

	public AssociatedPicoblazeProgramRom(RtlRealm realm, RtlClockNetwork clockNetwork, Class<?> anchorClass, String programSuffix) {
		super(realm);
		if (anchorClass == null) {
			anchorClass = getClass();
		}
		rom = ProgramRomUtil.loadAssociatedProgramRom(clockNetwork, anchorClass, programSuffix);
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

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
