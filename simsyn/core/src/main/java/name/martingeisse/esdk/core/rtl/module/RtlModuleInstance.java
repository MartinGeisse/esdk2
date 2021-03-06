/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogSignalDeclarationKeyword;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class RtlModuleInstance extends RtlItem {

	private String moduleName;
	private final Map<String, RtlInstancePort> ports = new HashMap<>();
	private final Map<String, Object> parameters = new HashMap<>();
	private final Map<String, Object> attributes = new HashMap<>();

	public RtlModuleInstance(RtlRealm realm) {
		super(realm);
		register();
	}

	public RtlModuleInstance(RtlRealm realm, String moduleName) {
		super(realm);
		register();
		this.moduleName = moduleName;
	}

	private void register() {
		RealmRegistrationKey key = new RealmRegistrationKey();
		getRealm().registerModuleInstance(key, this);
		key.valid = false;
	}

	/**
	 * This class is used to ensure that {@link RtlRealm#registerClockedItem(RtlClockedItem.RealmRegistrationKey, RtlClockedItem)}
	 * isn't called except through the {@link RtlClockedItem} constructor.
	 */
	public static final class RealmRegistrationKey {

		private boolean valid = true;

		private RealmRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Iterable<RtlInstancePort> getPorts() {
		return ports.values();
	}

	public RtlInstanceBitInputPort createBitInputPort(String portName) {
		return createBitInputPort(portName, null);
	}

	public RtlInstanceBitInputPort createBitInputPort(String portName, RtlBitSignal assignedSignal) {
		return new RtlInstanceBitInputPort(this, portName, assignedSignal);
	}

	public RtlInstanceBitInputPort createBitInputPort(String portName, boolean constantValue) {
		return createBitInputPort(portName, new RtlBitConstant(getRealm(), constantValue));
	}

	public RtlInstanceVectorInputPort createVectorInputPort(String portName, int width) {
		return createVectorInputPort(portName, width, null);
	}

	public RtlInstanceVectorInputPort createVectorInputPort(String portName, int width, RtlVectorSignal assignedSignal) {
		return new RtlInstanceVectorInputPort(this, portName, width, assignedSignal);
	}

	public RtlInstanceBitOutputPort createBitOutputPort(String portName) {
		return new RtlInstanceBitOutputPort(this, portName);
	}

	public RtlInstanceVectorOutputPort createVectorOutputPort(String portName, int width) {
		return new RtlInstanceVectorOutputPort(this, portName, width);
	}

	void addPort(String portName, RtlInstancePort port) {
		RtlInstancePort oldPort = ports.put(portName, port);
		if (oldPort != null) {
			ports.put(portName, oldPort);
			throw new IllegalStateException("a port with name " + portName + " was already added");
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.assignGeneratedName(RtlModuleInstance.this);
				for (RtlInstancePort port : ports.values()) {
					if (port instanceof RtlInstanceOutputPort) {
						RtlInstanceOutputPort outputPort = (RtlInstanceOutputPort) port;
						context.declareSignal(outputPort, VerilogSignalDeclarationKeyword.WIRE, false);
					}
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				for (RtlInstancePort port : ports.values()) {
					if (port instanceof RtlInstanceInputPort) {
						RtlInstanceInputPort inputPort = (RtlInstanceInputPort) port;
						consumer.consumeSignalUsage(inputPort.getAssignedSignal(), VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
					}
				}
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				if (!attributes.isEmpty()) {
					for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
						out.print("(* ");
						out.print(attribute.getKey());
						out.print('=');
						writeAttributeOrParameterValue(out, attribute.getValue());
						out.print(" *) ");
					}
					out.println();
				}
				if (parameters.isEmpty()) {
					out.print(moduleName + ' ');
					out.printName(RtlModuleInstance.this);
					out.print(" (");
				} else {
					out.print(moduleName + " #(");
					boolean firstParameter = true;
					for (Map.Entry<String, Object> entry : parameters.entrySet()) {
						if (firstParameter) {
							firstParameter = false;
							out.println();
						} else {
							out.println(",");
						}
						out.print("\t." + entry.getKey() + '(');
						writeAttributeOrParameterValue(out, entry.getValue());
						out.print(')');
					}
					out.println();
					out.print(") ");
                    out.printName(RtlModuleInstance.this);
                    out.print(" (");
				}
				boolean firstPort = true;
				for (RtlInstancePort port : ports.values()) {
					if (firstPort) {
						firstPort = false;
						out.println();
					} else {
						out.println(",");
					}
					out.print('\t');
					port.printPortAssignment(out);
				}
				out.println();
				out.println(");");
			}

			private void writeAttributeOrParameterValue(VerilogWriter out, Object value) {
				if (value instanceof String) {
					out.print("\"" + value + "\"");
				} else if (value instanceof Integer) {
					out.print(value);
				} else if (value instanceof VectorValue) {
					((VectorValue) value).printVerilogExpression(out);
				} else {
					throw new RuntimeException("invalid module attribute or parameter value: " + value);
				}
			}

		};
	}

}
