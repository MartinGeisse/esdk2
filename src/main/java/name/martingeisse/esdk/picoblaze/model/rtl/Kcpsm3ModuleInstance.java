/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.picoblaze.model.rtl;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.*;

/**
 *
 */
public final class Kcpsm3ModuleInstance {

	private final RtlModuleInstance moduleInstance;
	private final RtlInstanceBitInputPort clockPort;
	private final RtlInstanceBitInputPort resetPort;
	private final RtlInstanceVectorOutputPort instructionAddressPort;
	private final RtlInstanceVectorInputPort instructionPort;
	private final RtlInstanceBitOutputPort readStrobePort;
	private final RtlInstanceBitOutputPort writeStrobePort;
	private final RtlInstanceVectorOutputPort portIdPort;
	private final RtlInstanceVectorInputPort dataInputPort;
	private final RtlInstanceVectorOutputPort dataOutputPort;
	private final RtlInstanceBitInputPort interruptPort;
	private final RtlInstanceBitOutputPort interruptAckPort;

	public Kcpsm3ModuleInstance(RtlRealm realm) {
		this.moduleInstance = new RtlModuleInstance(realm, "kcpsm3");
		this.clockPort = moduleInstance.createBitInputPort("clk");
		this.resetPort = moduleInstance.createBitInputPort("reset");
		this.instructionAddressPort = moduleInstance.createVectorOutputPort("address", 10);
		this.instructionPort = moduleInstance.createVectorInputPort("instruction", 18);
		this.readStrobePort = moduleInstance.createBitOutputPort("read_strobe");
		this.writeStrobePort = moduleInstance.createBitOutputPort("write_strobe");
		this.portIdPort = moduleInstance.createVectorOutputPort("port_id", 8);
		this.dataInputPort = moduleInstance.createVectorInputPort("in_port", 8);
		this.dataOutputPort = moduleInstance.createVectorOutputPort("out_port", 8);
		this.interruptPort = moduleInstance.createBitInputPort("interrupt");
		this.interruptAckPort = moduleInstance.createBitOutputPort("interrupt_ack");
	}

	public RtlInstanceBitInputPort getClockPort() {
		return clockPort;
	}

	public RtlInstanceBitInputPort getResetPort() {
		return resetPort;
	}

	public RtlInstanceVectorOutputPort getInstructionAddressPort() {
		return instructionAddressPort;
	}

	public RtlInstanceVectorInputPort getInstructionPort() {
		return instructionPort;
	}

	public RtlInstanceBitOutputPort getReadStrobePort() {
		return readStrobePort;
	}

	public RtlInstanceBitOutputPort getWriteStrobePort() {
		return writeStrobePort;
	}

	public RtlInstanceVectorOutputPort getPortIdPort() {
		return portIdPort;
	}

	public RtlInstanceVectorInputPort getDataInputPort() {
		return dataInputPort;
	}

	public RtlInstanceVectorOutputPort getDataOutputPort() {
		return dataOutputPort;
	}

	public RtlInstanceBitInputPort getInterruptPort() {
		return interruptPort;
	}

	public RtlInstanceBitOutputPort getInterruptAckPort() {
		return interruptAckPort;
	}

}
