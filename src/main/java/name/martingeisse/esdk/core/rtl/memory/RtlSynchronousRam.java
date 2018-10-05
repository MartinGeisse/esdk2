/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedComputedVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlSynchronousRam extends RtlAbstractSynchronousRam {

	private VectorValue readData;
	private VectorValue nextReadData;

	public RtlSynchronousRam(RtlClockNetwork clockNetwork, Matrix matrix) {
		super(clockNetwork, matrix);
		this.readData = this.nextReadData = VectorValue.ofUnsigned(matrix.getColumnCount(), 0);
	}

	public RtlSynchronousRam(RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		super(clockNetwork, rowCount, columnCount);
		this.readData = this.nextReadData = VectorValue.ofUnsigned(getMatrix().getColumnCount(), 0);
	}

	public RtlVectorSignal getReadDataSignal() {
		return RtlSimulatedComputedVectorSignal.of(getRealm(), getMatrix().getColumnCount(), () -> readData);
	}

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
		super.computeNextState();
		nextReadData = getMatrix().getRow(getAddressSignal().getValue().getAsUnsignedInt());
	}

	@Override
	public void updateState() {
		super.updateState();
		readData = nextReadData;
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		// TODO
		throw newSynthesisNotSupportedException();
	}

}
