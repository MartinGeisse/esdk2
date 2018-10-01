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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlModuleInstance extends RtlItem {

	private String moduleName;
	private final List<RtlInstanceInputPort> inputPorts = new ArrayList<>();
	private final List<RtlInstanceOutputPort> outputPorts = new ArrayList<>();

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

	public Iterable<RtlInstanceInputPort> getInputPorts() {
		return inputPorts;
	}

	public RtlInstanceBitInputPort createBitInputPort(String portName, RtlBitSignal assignedSignal) {
		RtlInstanceBitInputPort inputPort = new RtlInstanceBitInputPort(this, portName, assignedSignal);
		inputPorts.add(inputPort);
		return inputPort;
	}

	public RtlInstanceVectorInputPort createVectorInputPort(String portName, RtlVectorSignal assignedSignal) {
		RtlInstanceVectorInputPort inputPort = new RtlInstanceVectorInputPort(this, portName, assignedSignal);
		inputPorts.add(inputPort);
		return inputPort;
	}

	public Iterable<RtlInstanceOutputPort> getOutputPorts() {
		return outputPorts;
	}

	public RtlInstanceBitOutputPort createBitOutputPort(String portName) {
		RtlInstanceBitOutputPort outputPort = new RtlInstanceBitOutputPort(this, portName);
		outputPorts.add(outputPort);
		return outputPort;
	}

	public RtlInstanceVectorOutputPort createVectorOutputPort(String portName, int width) {
		RtlInstanceVectorOutputPort outputPort = new RtlInstanceVectorOutputPort(this, portName, width);
		outputPorts.add(outputPort);
		return outputPort;
	}

}
