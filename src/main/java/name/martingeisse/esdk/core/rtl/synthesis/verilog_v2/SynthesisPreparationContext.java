/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public interface SynthesisPreparationContext {

	String implicitlyDeclareSignalWithAssignedName(RtlSignal signal, String prefix);

	String explicitlyDeclareSignalWithAssignedName(RtlSignal signal, String prefix, VerilogSignalKind signalKind);

	void implicitlyDeclareSignalWithFixedName(RtlSignal signal, String name);

	void explicitlyDeclareSignalWithFixedName(RtlSignal signal, String name, VerilogSignalKind signalKind);

	String assignGeneratedName(String prefix);

	void assignFixedName(String name);

}
