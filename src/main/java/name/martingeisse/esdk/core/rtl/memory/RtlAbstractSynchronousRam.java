/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public abstract class RtlAbstractSynchronousRam extends RtlClockedItem {

	private final Matrix matrix;
	private RtlVectorSignal addressSignal;
	private RtlBitSignal writeEnableSignal;
	private RtlVectorSignal writeDataSignal;
	private boolean writeEnable;
	private int writeAddress;
	private VectorValue writeData;

	public RtlAbstractSynchronousRam(RtlRealm realm, RtlClockNetwork clockNetwork, Matrix matrix) {
		super(realm, clockNetwork);
		this.matrix = matrix;
	}

	public RtlAbstractSynchronousRam(RtlRealm realm, RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		this(realm, clockNetwork, new Matrix(rowCount, columnCount));
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

	public RtlVectorSignal getWriteDataSignal() {
		return writeDataSignal;
	}

	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		if (writeDataSignal != null && writeDataSignal.getWidth() != matrix.getColumnCount()) {
			throw new IllegalArgumentException("write data signal has width " + writeDataSignal.getWidth() +
				" but matrix column count is " + matrix.getColumnCount());
		}
		this.writeDataSignal = writeDataSignal;
	}

	public RtlBitSignal getWriteEnableSignal() {
		return writeEnableSignal;
	}

	public void setWriteEnableSignal(RtlBitSignal writeEnableSignal) {
		this.writeEnableSignal = writeEnableSignal;
	}

	public abstract RtlVectorSignal getReadDataSignal();

	protected final int getAddress() {
		return addressSignal.getValue().getAsUnsignedInt();
	}

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
		if (addressSignal == null) {
			throw new IllegalStateException("no address signal");
		}
		if (writeEnableSignal == null) {
			throw new IllegalStateException("no write-enable signal");
		}
		writeEnable = writeEnableSignal.getValue();
		if (!writeEnable) {
			return;
		}
		if (writeDataSignal == null) {
			throw new IllegalStateException("no write data signal");
		}
		writeAddress = getAddress();
		writeData = writeDataSignal.getValue();
	}

	@Override
	public void updateState() {
		if (writeEnable) {
			matrix.setRow(writeAddress, writeData);
		}
	}

}
