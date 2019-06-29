/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public interface SynthesisPreparationContext {

    void assignFixedName(String name, VerilogNamed object);

    String assignGeneratedName(String fallbackPrefix, VerilogNamed object);

    void declareFixedNameSignal(RtlSignal signal,
                                String name,
                                VerilogSignalDeclarationKeyword keyword,
                                boolean generateAssignment);

    String declareSignal(RtlSignal signal,
                         String fallbackPrefix,
                         VerilogSignalDeclarationKeyword keyword,
                         boolean generateAssignment);

    String declareProceduralMemory(RtlProceduralMemory memory);

    AuxiliaryFileFactory getAuxiliaryFileFactory();

}
