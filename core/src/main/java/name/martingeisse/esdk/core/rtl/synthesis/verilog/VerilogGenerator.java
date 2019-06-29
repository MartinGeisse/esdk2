/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

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
	private final Map<RtlSignal, VerilogGenerator.SignalDeclaration> signalDeclarations = new HashMap<>();
	private final Names names = new Names();

	public VerilogGenerator(Writer out, RtlRealm realm, String toplevelModuleName, AuxiliaryFileFactory auxiliaryFileFactory) {
		this.out = new VerilogWriter(out) {

			@Override
			protected String getSignalName(RtlSignal signal) {
				SignalDeclaration signalDeclaration = signalDeclarations.get(signal);
				return (signalDeclaration == null) ? null : signalDeclaration.name;
			}

			@Override
			protected String getMemoryName(RtlProceduralMemory memory) {
			    return names.getMemoryName(memory);
			}

		};
		this.realm = realm;
		this.toplevelModuleName = toplevelModuleName;
		this.auxiliaryFileFactory = auxiliaryFileFactory;
	}

	public void generate() {

		// validate and materialize items
		realm.getDesign().validateOrException(false);
		realm.getDesign().materialize();

		// collect contributions from all items
		List<VerilogContribution> contributions = new ArrayList<>();
		for (RtlItem item : realm.getItems()) {
			VerilogContribution contribution = item.getVerilogContribution();
			if (contribution == null) {
				throw new RuntimeException("Got null verilog contribution which is not allowed; item = " + item);
			}
			contributions.add(contribution);
		}

		// prepare contributions. This also collects signals that must be declared.
		{
			SynthesisPreparationContext synthesisPreparationContext = new SynthesisPreparationContext() {

				@Override
				public void declareFixedNameSignal(RtlSignal signal, String name, VerilogSignalKind signalKindForExplicitDeclarationOrNullForNoDeclaration, boolean generateAssignment) {
					reserveName(name, false);
					internalDeclareSignal(signal, name, signalKindForExplicitDeclarationOrNullForNoDeclaration, generateAssignment);
				}

				@Override
				public String declareSignal(RtlSignal signal, String fallbackPrefix, VerilogSignalKind signalKindForExplicitDeclarationOrNullForNoDeclaration, boolean generateAssignment) {
					String signalName = signal.getRtlItem().getName();
					String prefix = signalName == null ? fallbackPrefix : signalName;
					String globalName = reserveName(prefix, true);
					internalDeclareSignal(signal, globalName, signalKindForExplicitDeclarationOrNullForNoDeclaration, generateAssignment);
					return globalName;
				}

				private void internalDeclareSignal(RtlSignal signal, String name, VerilogSignalKind signalKindForExplicitDeclarationOrNullForNoDeclaration, boolean generateAssignment) {
					signalDeclarations.put(signal, new SignalDeclaration(signal, name,
						signalKindForExplicitDeclarationOrNullForNoDeclaration != null,
						signalKindForExplicitDeclarationOrNullForNoDeclaration,
						generateAssignment
					));
				}

				@Override
				public String declareProceduralMemory(RtlProceduralMemory memory) {
					return names.declareProceduralMemory(memory);
				}

				@Override
				public String reserveName(String nameOrPrefix, boolean appendCounterSuffix) {
					if (appendCounterSuffix) {
						return names.assignGeneratedName(nameOrPrefix);
					} else {
						return names.assignFixedName(nameOrPrefix);
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
					public VerilogExpressionWriter printSignal(RtlSignal subSignal, VerilogExpressionNesting subNesting) {
						consumeSignalUsage(subSignal, subNesting);
						return this;
					}

					@Override
					public VerilogExpressionWriter printMemory(RtlProceduralMemory memory) {
						return this;
					}
				};

				@Override
				public void consumeSignalUsage(RtlSignal signal, VerilogExpressionNesting nesting) {

					// for convenience, so this "if" does not have to be repeated over and over again
					if (signal == null) {
						return;
					}

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

					// analyze signals for shared sub-expressions
					signal.analyzeSignalUsage(this);

				}

				private void declareSignal(RtlSignal signal) {
					if (signalDeclarations.get(signal) == null) {
						String prefix = signal.getRtlItem().getName();
						if (prefix == null) {
							prefix = "s";
						}
						signalDeclarations.put(signal, new SignalDeclaration(signal, names.assignGeneratedName(prefix), true, VerilogSignalKind.WIRE, true));
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

		// consume toplevel ports
		List<ToplevelPortContribution> toplevelPorts = new ArrayList<>();
		for (VerilogContribution contribution : contributions) {
			contribution.analyzeToplevelPorts((direction, name, width) -> toplevelPorts.add(new ToplevelPortContribution(direction, name, width)));
		}

		// assemble the toplevel module
		out.println("`default_nettype none");
		out.println("`timescale 1ns / 1ps");
		out.println();
		out.println("module " + toplevelModuleName + "(");
		if (!toplevelPorts.isEmpty()) {
			out.startIndentation();
			boolean first = true;
			for (ToplevelPortContribution toplevelPort : toplevelPorts) {
				if (first) {
					first = false;
				} else {
					out.println(',');
				}
				out.indent();
				out.print(toplevelPort.name);
			}
			out.println();
			out.endIndentation();
		}
		out.println(");");
		out.println();
		if (!toplevelPorts.isEmpty()) {
			for (ToplevelPortContribution toplevelPort : toplevelPorts) {
				if (toplevelPort.width == null) {
					out.println(toplevelPort.direction + ' ' + toplevelPort.name + ';');
				} else {
					out.println(toplevelPort.direction + '[' + (toplevelPort.width - 1) + ":0] " + toplevelPort.name + ';');
				}
			}
			out.println();
		}
		out.println();
		for (Map.Entry<RtlSignal, SignalDeclaration> signalEntry : signalDeclarations.entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			SignalDeclaration signalDeclaration = signalEntry.getValue();
			if (signalDeclaration.explicitDeclaration) {
				out.print(signalDeclaration.kind.name().toLowerCase());
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
				out.print(signalDeclaration.name);
				out.println(';');
			}
		}
		out.println();
		for (VerilogContribution contribution : contributions) {
			contribution.printDeclarations(out);
		}
		out.println();
		for (Map.Entry<RtlSignal, SignalDeclaration> signalEntry : signalDeclarations.entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			SignalDeclaration signalDeclaration = signalEntry.getValue();
			if (signalDeclaration.assignment) {
				out.print("assign " + signalDeclaration.name + " = ");
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

	static class SignalDeclaration {

		final RtlSignal signal;
		final String name;
		final boolean explicitDeclaration;
		final VerilogSignalKind kind;
		final boolean assignment;

		SignalDeclaration(RtlSignal signal, String name, boolean explicitDeclaration, VerilogSignalKind kind, boolean assignment) {
			this.signal = signal;
			this.name = name;
			this.explicitDeclaration = explicitDeclaration;
			this.kind = kind;
			this.assignment = assignment;
		}

	}

	static class ToplevelPortContribution {

		final String direction;
		final String name;
		final Integer width;

		public ToplevelPortContribution(String direction, String name, Integer width) {
			this.direction = direction;
			this.name = name;
			this.width = width;
		}

	}

}
