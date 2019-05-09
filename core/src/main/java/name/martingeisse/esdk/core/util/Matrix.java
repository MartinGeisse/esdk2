/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util;

import name.martingeisse.esdk.core.util.vector.VectorValue;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;

/**
 * A mutable bit matrix. This is basically a mutable fixed-size array of {@link VectorValue}s which all have the same
 * pre-configured width. Access happens in rows, not individual bits.
 */
public final class Matrix {

	private final int rowCount;
	private final int columnCount;
	private final VectorValue[] rows;
	private final VectorValue defaultRowValue;

	public Matrix(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.rows = new VectorValue[rowCount];
		this.defaultRowValue = VectorValue.of(columnCount, 0);
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	private void checkRowIndex(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= rowCount) {
			throw new IllegalArgumentException("invalid row index " + rowIndex + " for row count " + rowCount);
		}
	}

	public VectorValue getRow(int rowIndex) {
		checkRowIndex(rowIndex);
		VectorValue row = rows[rowIndex];
		return (row == null ? defaultRowValue : row);
	}

	public void setRow(int rowIndex, VectorValue row) {
		checkRowIndex(rowIndex);
		if (row == null) {
			throw new IllegalArgumentException("row cannot be null");
		}
		if (row.getWidth() != columnCount) {
			throw new IllegalArgumentException("row has wrong width " + row.getWidth() + ", expected " + columnCount);
		}
		rows[rowIndex] = row;
	}

	public void writeToMif(PrintWriter out) {
		int matrixDigitCount = (columnCount + 3) / 4;
		String allZeros = StringUtils.repeat('0', matrixDigitCount);
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			VectorValue row = rows[rowIndex];
			if (row == null) {
				row = defaultRowValue;
			}
			String digits = row.getDigits();
			String zeros = allZeros.substring(digits.length());
			out.print(zeros);
			out.println(digits);
		}
	}

}
