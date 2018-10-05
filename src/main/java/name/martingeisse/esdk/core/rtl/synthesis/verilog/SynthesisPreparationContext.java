/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public interface SynthesisPreparationContext {

	String declareSignal(RtlSignal signal,
						 String nameOrPrefix, boolean appendCounterSuffix,
						 VerilogSignalKind signalKindForExplicitDeclarationOrNullForNoDeclaration,
						 boolean generateAssignment);

	String reserveName(String nameOrPrefix, boolean appendCounterSuffix);

	AuxiliaryFileFactory getAuxiliaryFileFactory();

}
