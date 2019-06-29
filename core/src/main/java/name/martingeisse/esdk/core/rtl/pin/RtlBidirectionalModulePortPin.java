/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 * This is a special case of a bidirectional pin that is directly connected to a bidirectional
 * module port.
 */
public final class RtlBidirectionalModulePortPin extends RtlPin implements VerilogNamed {

	private final RtlInstancePort port;

	public RtlBidirectionalModulePortPin(RtlRealm realm, RtlModuleInstance moduleInstance, String portName) {
		super(realm);
		port = new RtlInstancePort(moduleInstance, portName) {
			@Override
			protected void printPortAssignment(VerilogWriter out) {
				out.print("." + getPortName() + "(");
				out.print(RtlBidirectionalModulePortPin.this.getNetName());
				out.print(')');
			}
		};
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.assignFixedName(getNetName(), RtlBidirectionalModulePortPin.this);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("inout", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

	@Override
	public Item getVerilogNameSuggestionProvider() {
		return this;
	}

}
