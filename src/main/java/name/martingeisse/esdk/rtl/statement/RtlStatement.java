/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlItem;
import name.martingeisse.esdk.rtl.RtlSignal;
import name.martingeisse.esdk.rtl.VerilogWriter;

import java.util.function.Consumer;

/**
 *
 */
public abstract class RtlStatement extends RtlItem {

	public RtlStatement(RtlDesign design) {
		super(design);
	}

	public abstract void foreachSignalDependency(Consumer<RtlSignal> consumer);

	public abstract void printVerilogStatements(VerilogWriter out);

}
