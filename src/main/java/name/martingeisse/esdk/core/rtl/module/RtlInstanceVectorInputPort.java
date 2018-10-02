/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public final class RtlInstanceVectorInputPort extends RtlInstanceInputPort {

	private final int width;
	private RtlVectorSignal assignedSignal;

	public RtlInstanceVectorInputPort(RtlModuleInstance moduleInstance, String portName, int width) {
		super(moduleInstance, portName);
		this.width = width;
	}

	public RtlInstanceVectorInputPort(RtlModuleInstance moduleInstance, String portName, int width, RtlVectorSignal assignedSignal) {
		this(moduleInstance, portName, width);
		setAssignedSignal(assignedSignal);
	}

	@Override
	public RtlVectorSignal getAssignedSignal() {
		return assignedSignal;
	}

	public void setAssignedSignal(RtlVectorSignal assignedSignal) {
		if (assignedSignal != null && assignedSignal.getWidth() != width) {
			throw new IllegalArgumentException("expected signal width " + width + ", got " + assignedSignal.getWidth());
		}
		this.assignedSignal = assignedSignal;
	}

}
