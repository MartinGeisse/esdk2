/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlSynchronousRom extends RtlClockedItem {

	private final Matrix matrix;
	private RtlVectorSignal addressSignal;
	private VectorValue readData;
	private VectorValue nextReadData;

	public RtlSynchronousRom(RtlRealm realm, RtlClockNetwork clockNetwork, Matrix matrix) {
		super(realm, clockNetwork);
		this.matrix = matrix;
		this.readData = this.nextReadData = VectorValue.ofUnsigned(matrix.getColumnCount(), 0);
	}

	public RtlSynchronousRom(RtlRealm realm, RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		this(realm, clockNetwork, new Matrix(rowCount, columnCount));
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public RtlVectorSignal getAddressSignal() {
		return addressSignal;
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		if (addressSignal.getWidth() > 30) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() + " not supported");
		}
		if (1 << addressSignal.getWidth() > matrix.getRowCount()) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() +
				" is too large for matrix row count " + matrix.getRowCount());
		}
		this.addressSignal = addressSignal;
	}

	public RtlVectorSignal getReadDataSignal() {
		return RtlCustomVectorSignal.of(getRealm(), matrix.getColumnCount(), () -> readData);
	}

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
		nextReadData = matrix.getRow(addressSignal.getValue().getAsUnsignedInt());
	}

	@Override
	public void updateState() {
		readData = nextReadData;
	}

}
