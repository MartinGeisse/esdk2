/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.*;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class RtlModuleInstance extends RtlItem {

	private String moduleName;
	private final Map<String, RtlInstancePort> ports = new HashMap<>();

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

	public Iterable<RtlInstancePort> getPorts() {
		return ports.values();
	}

	public RtlInstanceBitInputPort createBitInputPort(String portName) {
		return createBitInputPort(portName, null);
	}

	public RtlInstanceBitInputPort createBitInputPort(String portName, RtlBitSignal assignedSignal) {
		RtlInstanceBitInputPort inputPort = new RtlInstanceBitInputPort(this, portName, assignedSignal);
		addPort(portName, inputPort);
		return inputPort;
	}

	public RtlInstanceVectorInputPort createVectorInputPort(String portName, int width) {
		return createVectorInputPort(portName, width, null);
	}

	public RtlInstanceVectorInputPort createVectorInputPort(String portName, int width, RtlVectorSignal assignedSignal) {
		RtlInstanceVectorInputPort inputPort = new RtlInstanceVectorInputPort(this, portName, width, assignedSignal);
		addPort(portName, inputPort);
		return inputPort;
	}

	public RtlInstanceBitOutputPort createBitOutputPort(String portName) {
		RtlInstanceBitOutputPort outputPort = new RtlInstanceBitOutputPort(this, portName);
		addPort(portName, outputPort);
		return outputPort;
	}

	public RtlInstanceVectorOutputPort createVectorOutputPort(String portName, int width) {
		RtlInstanceVectorOutputPort outputPort = new RtlInstanceVectorOutputPort(this, portName, width);
		addPort(portName, outputPort);
		return outputPort;
	}

	private void addPort(String portName, RtlInstancePort port) {
		RtlInstancePort oldPort = ports.put(portName, port);
		if (oldPort != null) {
			ports.put(portName, oldPort);
			throw new IllegalStateException("a port with name " + portName + " was already added");
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			private String instanceName;

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				instanceName = context.reserveName("m", true);
				for (RtlInstancePort port : ports.values()) {
					if (port instanceof RtlInstanceOutputPort) {
						RtlInstanceOutputPort outputPort = (RtlInstanceOutputPort) port;
						context.declareSignal(outputPort, "mp", true, VerilogSignalKind.WIRE, false);
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
			public void analyzePins(PinConsumer consumer) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.print(moduleName + ' ' + instanceName + '(');
				boolean firstPort = true;
				for (RtlInstancePort port : ports.values()) {
					if (firstPort) {
						firstPort = false;
						out.println();
					} else {
						out.println(",");
					}
					out.print("\t." + port.getPortName() + "(");
					if (port instanceof RtlInstanceInputPort) {
						RtlInstanceInputPort inputPort = (RtlInstanceInputPort) port;
						if (inputPort.getAssignedSignal() == null) {
							throw new IllegalStateException("input port " + inputPort.getPortName() +
								" of instance of module " + moduleName + " has no assigned signal");
						}
						out.print(inputPort.getAssignedSignal());
					} else if (port instanceof RtlInstanceOutputPort) {
						RtlInstanceOutputPort outputPort = (RtlInstanceOutputPort) port;
						out.print(outputPort);
					} else {
						throw new RuntimeException("unknown instance port type");
					}
					out.print(')');
				}
				out.println();
				out.println(");");
			}

		};
	}
}
