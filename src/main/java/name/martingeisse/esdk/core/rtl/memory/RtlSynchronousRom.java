/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public final class RtlSynchronousRom extends RtlClockedItem {

	private final Matrix matrix;
	private final RtlVectorSignal readDataSignal;
	private RtlVectorSignal addressSignal;
	private VectorValue readData;
	private VectorValue nextReadData;

	public RtlSynchronousRom(RtlClockNetwork clockNetwork, Matrix matrix) {
		super(clockNetwork);
		this.matrix = matrix;
		this.readDataSignal = new ReadDataSignal(getRealm());
		this.readData = this.nextReadData = VectorValue.ofUnsigned(matrix.getColumnCount(), 0);
	}

	public RtlSynchronousRom(RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		this(clockNetwork, new Matrix(rowCount, columnCount));
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
		return readDataSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

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

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
		expressionWriter.print(addressSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

	public void printImplementation(VerilogWriter out) {
		String memoryName = out.newMemoryName();
		String mifName = memoryName + ".mif";

		// memory
		out.getOut().println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
			(matrix.getRowCount() - 1) + ":0];");

		// initialization
		out.getOut().println("initial $readmemh(\"" + mifName + "\", rom, 0, " + (matrix.getRowCount() - 1) + ");\n");

		//
		out.getOut().print("always @(posedge ");
		out.printExpression(getClockNetwork().getClockSignal());
		out.getOut().println(" begin");

		//
		out.getOut().print('\t');
		out.printExpression(readDataSignal);
		out.getOut().print(" <= " + memoryName + "[");
		out.printExpression(addressSignal);
		out.getOut().println("];");

		//
		out.getOut().println("end");

		try {
			out.generateMif(mifName, matrix);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Iterable<? extends RtlSignal> getSignalsThatRequireDeclarationInVerilog() {
		return Arrays.asList(readDataSignal);
	}

	//
	//
	//

	public final class ReadDataSignal extends RtlCustomVectorSignal {

		public ReadDataSignal(RtlRealm realm) {
			super(realm);
		}

		@Override
		public int getWidth() {
			return matrix.getColumnCount();
		}

		@Override
		public VectorValue getValue() {
			return readData;
		}

		@Override
		public boolean isGenerateVerilogAssignmentForDeclaration() {
			return false;
		}

	};

}
