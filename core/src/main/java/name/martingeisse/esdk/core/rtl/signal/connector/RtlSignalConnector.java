/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.connector;

import name.martingeisse.esdk.core.model.validation.ValidationContext;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 * This signal simply produces the same values as another signal which is settable after construction. This helps
 * constructing signal networks since most other signal classes are immutable after construction.
 * <p>
 * This class is not meant to dynamically change the connected signal. Even though simulation won't choke on that,
 * it is not synthesizable (synthesis will simply use the signal connected at the time the synthesis is run).
 */
public abstract class RtlSignalConnector extends RtlItem implements RtlSignal {

	public RtlSignalConnector(RtlRealm realm) {
		super(realm);
	}

	public abstract RtlSignal getConnected();

	@Override
	public void validate(ValidationContext context) {
		if (getConnected() == null) {
			context.reportError(getName() + ": no signal connected");
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return getConnected().compliesWith(nesting);
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		// we allow "all" here because we detect invalid nesting in compliesWith()
		out.print(getConnected(), VerilogExpressionNesting.ALL);
	}

}
