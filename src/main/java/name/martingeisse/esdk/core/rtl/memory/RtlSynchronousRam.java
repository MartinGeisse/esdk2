/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlSynchronousRam extends RtlAbstractSynchronousRam {

	private VectorValue readData;
	private VectorValue nextReadData;

	public RtlSynchronousRam(RtlRealm realm, RtlClockNetwork clockNetwork, Matrix matrix) {
		super(realm, clockNetwork, matrix);
		this.readData = this.nextReadData = VectorValue.ofUnsigned(matrix.getColumnCount(), 0);
	}

	public RtlSynchronousRam(RtlRealm realm, RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		super(realm, clockNetwork, rowCount, columnCount);
		this.readData = this.nextReadData = VectorValue.ofUnsigned(getMatrix().getColumnCount(), 0);
	}

	public RtlVectorSignal getReadDataSignal() {
		return RtlCustomVectorSignal.of(getRealm(), getMatrix().getColumnCount(), () -> readData);
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

}
