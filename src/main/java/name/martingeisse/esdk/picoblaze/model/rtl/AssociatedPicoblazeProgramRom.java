/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlMultiportMemory;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlSynchronousMemoryPort;
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

	private final RtlMultiportMemory rom;
	private final RtlSynchronousMemoryPort romPort;

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
		rom = ProgramRomUtil.loadAssociatedProgramRom(realm, anchorClass, programSuffix);
		romPort = rom.createSynchronousPort(clockNetwork, RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS);
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		romPort.setAddressSignal(addressSignal);
	}

	public RtlVectorSignal getInstructionSignal() {
		return romPort.getReadDataSignal();
	}

	public RtlMultiportMemory getRom() {
		return rom;
	}

	public RtlSynchronousMemoryPort getRomPort() {
		return romPort;
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
