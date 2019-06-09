/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic array of pins that are neither assigned an output signal nor can be used as an input signal. Custom code
 * is needed to make use of these pins.
 */
public final class RtlGenericPinArray extends RtlItem {

	private final String verilogDirectionKeyword;
	private final String netName;
	private final ImmutableList<RtlPin> pins;

	public RtlGenericPinArray(RtlRealm realm, String verilogDirectionKeyword, String netName, String... pinIds) {
		super(realm);
		this.verilogDirectionKeyword = verilogDirectionKeyword;
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

	public String getVerilogDirectionKeyword() {
		return verilogDirectionKeyword;
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
				context.reserveName(netName, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort(verilogDirectionKeyword, netName, pins.size());
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
