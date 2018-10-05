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
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.*;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

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

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			private String memoryName;

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				memoryName = context.reserveName("mem", true);
				context.declareSignal(readDataSignal, "s", true, VerilogSignalKind.REG, false);
				MemoryImplementationUtil.generateMif(context.getAuxiliaryFileFactory(), memoryName + ".mif", matrix);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(addressSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
			}

			@Override
			public void analyzePins(PinConsumer consumer) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {

				// memory
				out.println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
					(matrix.getRowCount() - 1) + ":0];");

				// initialization
				out.println("initial $readmemh(\"" + memoryName + ".mif\", " + memoryName + ", 0, " + (matrix.getRowCount() - 1) + ");\n");

				//
				out.print("always @(posedge ");
				out.print(getClockNetwork().getClockSignal());
				out.println(") begin");

				//
				out.print('\t');
				out.print(readDataSignal);
				out.print(" <= " + memoryName + "[");
				out.print(addressSignal);
				out.println("];");

				//
				out.println("end");

			}

		};
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
		public VerilogSignalKind getVerilogSignalKind() {
			return VerilogSignalKind.REG;
		}

		@Override
		public boolean isGenerateVerilogAssignmentForDeclaration() {
			return false;
		}

	}

	;

}
