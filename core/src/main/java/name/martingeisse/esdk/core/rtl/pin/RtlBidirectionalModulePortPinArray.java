/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a special case of an array of bidirectional pins that is directly connected to a (vector-typed)
 * bidirectional module port.
 */
public final class RtlBidirectionalModulePortPinArray extends RtlItem implements VerilogNamed {

	private final RtlInstancePort port;
	private final String netName;
	private final ImmutableList<RtlPin> pins;

	public RtlBidirectionalModulePortPinArray(RtlRealm realm, RtlModuleInstance moduleInstance, String portName, String netName, String... pinIds) {
		super(realm);
		port = new RtlInstancePort(moduleInstance, portName) {
			@Override
			protected void printPortAssignment(VerilogWriter out) {
				out.print("." + getPortName() + "(" + netName + ")");
			}
		};
		this.netName = netName;
		List<RtlPin> pins = new ArrayList<>();
		for (int i = 0; i < pinIds.length; i++) {
			int index = i;
			RtlPin pin = new RtlPin(realm) {

				@Override
				public VerilogContribution getVerilogContribution() {
					return new EmptyVerilogContribution();
				}

				@Override
				public String getNetName() {
					return netName + "<" + index + ">";
				}

			};
			pin.setId(pinIds[i]);
			pins.add(pin);
		}
		this.pins = ImmutableList.copyOf(pins);
	}

	public RtlInstancePort getPort() {
		return port;
	}

	public String getNetName() {
		return netName;
	}

	public ImmutableList<RtlPin> getPins() {
		return pins;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.assignFixedName(netName, RtlBidirectionalModulePortPinArray.this);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("inout", netName, pins.size());
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

	@Override
	public RtlItem getVerilogNameSuggestionProvider() {
		return this;
	}

}
