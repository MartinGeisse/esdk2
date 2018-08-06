/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.Matrix;

/**
 *
 */
public final class RtlAsynchronousRom extends RtlItem {

	private final Matrix matrix;
	private RtlVectorSignal addressSignal;

	public RtlAsynchronousRom(RtlRealm realm, Matrix matrix) {
		super(realm);
		this.matrix = matrix;
	}

	public RtlAsynchronousRom(RtlRealm realm, int rowCount, int columnCount) {
		this(realm, new Matrix(rowCount, columnCount));
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public RtlVectorSignal getAddressSignal() {
		return addressSignal;
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		MemoryImplementationUtil.checkAddressSignal(addressSignal, matrix.getRowCount());
		this.addressSignal = addressSignal;
	}

	public RtlVectorSignal getReadDataSignal() {
		return RtlCustomVectorSignal.of(getRealm(), matrix.getColumnCount(),
			() -> matrix.getRow(addressSignal.getValue().getAsUnsignedInt()));
	}

}
