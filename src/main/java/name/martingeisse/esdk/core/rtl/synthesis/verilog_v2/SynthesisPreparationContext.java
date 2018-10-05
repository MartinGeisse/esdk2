/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogSignalKind;

/**
 *
 */
public interface SynthesisPreparationContext {

	String implicitlyDeclareSignalWithAssignedName(RtlSignal signal, String prefix);

	String explicitlyDeclareSignalWithAssignedName(RtlSignal signal, String prefix, VerilogSignalKind signalKind);

	String implicitlyDeclareSignalWithFixedName(RtlSignal signal, String name);

	String explicitlyDeclareSignalWithFixedName(RtlSignal signal, String name, VerilogSignalKind signalKind);

	String reserveAssignedName(String prefix);

	String reserveFixedName(String name);

}
