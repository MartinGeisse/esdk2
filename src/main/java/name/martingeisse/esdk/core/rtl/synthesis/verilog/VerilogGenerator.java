/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Writer;
import java.util.*;

/**
 *
 */
public class VerilogGenerator {

	private final VerilogWriter out;
	private final RtlRealm realm;
	private final String toplevelModuleName;
	private final AuxiliaryFileFactory auxiliaryFileFactory;

	private final Set<String> names = new HashSet<>();
	private final Set<String> fixedNames = new HashSet<>();
	private final Map<String, MutableInt> prefixNameCounters = new HashMap<>();
	private final Map<RtlSignal, NamedSignal> namedSignals = new HashMap<>();

	public VerilogGenerator(Writer out, RtlRealm realm, String toplevelModuleName, AuxiliaryFileFactory auxiliaryFileFactory) {
		this.out = new VerilogWriter(out) {
			@Override
			protected String getSignalName(RtlSignal signal) {
				NamedSignal namedSignal = namedSignals.get(signal);
				return (namedSignal == null) ? null : namedSignal.name;
			}
		};
		this.realm = realm;
		this.toplevelModuleName = toplevelModuleName;
		this.auxiliaryFileFactory = auxiliaryFileFactory;
	}

	public void generate() {

		// collect contributions from all items
		List<VerilogContribution> contributions = new ArrayList<>();
		for (RtlItem item : realm.getItems()) {
			VerilogContribution contribution = item.getVerilogContribution();
			if (contribution == null) {
				throw new RuntimeException("Got null verilog contribution which is not allowed.");
			}
			contributions.add(contribution);
		}

		// prepare contributions. This also collects signals that must be declared.
		{
			SynthesisPreparationContext synthesisPreparationContext = new SynthesisPreparationContext() {

				@Override
				public String declareSignal(RtlSignal signal, String nameOrPrefix, boolean appendCounterSuffix, VerilogSignalKind signalKindForExplicitDeclarationOrNullForNoDeclaration, boolean generateAssignment) {
					String name = reserveName(nameOrPrefix, appendCounterSuffix);
					namedSignals.put(signal, new NamedSignal(signal, name,
						signalKindForExplicitDeclarationOrNullForNoDeclaration != null,
						signalKindForExplicitDeclarationOrNullForNoDeclaration,
						generateAssignment
					));

					return null;
				}

				@Override
				public String reserveName(String nameOrPrefix, boolean appendCounterSuffix) {
					if (appendCounterSuffix) {
						return VerilogGenerator.this.assignGeneratedName(nameOrPrefix);
					} else {
						return VerilogGenerator.this.assignFixedName(nameOrPrefix);
					}
				}

				@Override
				public AuxiliaryFileFactory getAuxiliaryFileFactory() {
					return auxiliaryFileFactory;
				}

			};
			for (VerilogContribution contribution : contributions) {
				contribution.prepareSynthesis(synthesisPreparationContext);
			}
		}

		// Analyze all signals for shared usage. These signals will be declared too.
		{
			final Set<RtlSignal> analyzedSignals = new HashSet<>();
			SignalUsageConsumer signalUsageConsumer = new SignalUsageConsumer() {

				VerilogExpressionWriter fakeExpressionWriter = new VerilogExpressionWriter() {

					@Override
					public VerilogExpressionWriter print(String s) {
						return this;
					}

					@Override
					public VerilogExpressionWriter print(int i) {
						return this;
					}

					@Override
					public VerilogExpressionWriter print(char c) {
						return this;
					}

					@Override
					public VerilogExpressionWriter print(RtlSignal subSignal, VerilogExpressionNesting subNesting) {
						consumeSignalUsage(subSignal, subNesting);
						return this;
					}

				};

				@Override
				public void consumeSignalUsage(RtlSignal signal, VerilogExpressionNesting nesting) {

					// Extract all signals that are used in more than one place. Those have been analyzed already when we found them
					// the first time.
					if (!analyzedSignals.add(signal)) {
						declareSignal(signal);
						return;
					}

					// Also extract signals that do not comply with their current nesting level, but since we didn't find them
					// above, they have not been analyzed yet, so continue below.
					boolean compliesWithNesting = signal.compliesWith(nesting);
					if (!compliesWithNesting) {
						declareSignal(signal);
					}

					// Analyze signals for sub-expressions by calling a "dry-run" printing process. While this sounds more complex,
					// it concentrates the complexity here, in one place, and simplifies the RtlSignal implementations.
					signal.printVerilogImplementationExpression(fakeExpressionWriter);

				}

				private void declareSignal(RtlSignal signal) {
					if (namedSignals.get(signal) == null) {
						namedSignals.put(signal, new NamedSignal(signal, assignGeneratedName("s"), true, VerilogSignalKind.WIRE, true));
					}
				}

				@Override
				public VerilogExpressionWriter getFakeExpressionWriter() {
					return fakeExpressionWriter;
				}

			};
			for (VerilogContribution contribution : contributions) {
				contribution.analyzeSignalUsage(signalUsageConsumer);
			}
		}

		// consume pins
		List<Pair<String, String>> pins = new ArrayList<>();
		for (VerilogContribution contribution : contributions) {
			contribution.analyzePins((direction, name) -> pins.add(Pair.of(direction, name)));
		}

		// assemble the toplevel module
		out.println("`default_nettype none");
		out.println("`timescale 1ns / 1ps");
		out.println();
		out.println("module " + toplevelModuleName + "(");
		if (!pins.isEmpty()) {
			out.startIndentation();
			boolean first = true;
			for (Pair<String, String> pin : pins) {
				if (first) {
					first = false;
				} else {
					out.println(',');
				}
				out.indent();
				out.print(pin.getRight());
			}
			out.println();
			out.endIndentation();
		}
		out.println(");");
		out.println();
		if (!pins.isEmpty()) {
			for (Pair<String, String> pin : pins) {
				out.println(pin.getLeft() + ' ' + pin.getRight() + ';');
			}
			out.println();
		}
		out.println();
		for (Map.Entry<RtlSignal, NamedSignal> signalEntry : namedSignals.entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			NamedSignal namedSignal = signalEntry.getValue();
			if (namedSignal.explicitDeclaration) {
				out.print(namedSignal.kind.name().toLowerCase());
				if (signal instanceof RtlVectorSignal) {
					RtlVectorSignal vectorSignal = (RtlVectorSignal) signal;
					out.print('[');
					out.print(vectorSignal.getWidth() - 1);
					out.print(":0] ");
				} else if (signal instanceof RtlBitSignal) {
					out.print(' ');
				} else {
					throw new RuntimeException("signal is neither a bit signal nor a vector signal: " + signal);
				}
				out.print(namedSignal.name);
				out.println(';');
			}
		}
		out.println();
		for (Map.Entry<RtlSignal, NamedSignal> signalEntry : namedSignals.entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			NamedSignal namedSignal = signalEntry.getValue();
			if (namedSignal.assignment) {
				out.print("assign " + namedSignal.name + " = ");
				out.printImplementationExpression(signal);
				out.println(";");
			}
		}
		out.println();
		for (VerilogContribution contribution : contributions) {
			contribution.printImplementation(out);
		}
		out.println();
		out.println("endmodule");
		out.println();

	}

	//
	// names and signals
	//

	private String assignFixedName(String name) {
		if (!names.add(name)) {
			throw new IllegalStateException("fixed name is already used: " + name);
		}
		fixedNames.add(name);
		return name;
	}

	private String assignGeneratedName(String prefix) {
		MutableInt counter = prefixNameCounters.computeIfAbsent(prefix, p -> new MutableInt());
		while (true) {
			String name = prefix + counter.intValue();
			counter.increment();

			// If the counter collides with a fixed name, we shouldn't just increment again because the order
			// in which the fixed and assigned names get reserved should not be relevant -- and if the counter
			// comes first, #assignFixedName throws an exception too.
			if (fixedNames.contains(name)) {
				throw new IllegalStateException("assigned name collides with fixed name: " + name);
			}

			// There may still be a collision in the odd case of counter prefixes like "foo" and "foo1", so to
			// avoid edge cases, we have to check that too, hence the while loop.
			if (names.add(name)) {
				return name;
			}

		}
	}

	static class NamedSignal {

		final RtlSignal signal;
		final String name;
		final boolean explicitDeclaration;
		final VerilogSignalKind kind;
		final boolean assignment;

		NamedSignal(RtlSignal signal, String name, boolean explicitDeclaration, VerilogSignalKind kind, boolean assignment) {
			this.signal = signal;
			this.name = name;
			this.explicitDeclaration = explicitDeclaration;
			this.kind = kind;
			this.assignment = assignment;
		}

	}

}
