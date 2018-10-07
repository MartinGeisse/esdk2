/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlSynchronousRam extends RtlAbstractSynchronousRam {

	private VectorValue readData;
	private VectorValue nextReadData;
	private final RtlVectorSignal readDataSignal;

	public RtlSynchronousRam(RtlClockNetwork clockNetwork, Matrix matrix) {
		super(clockNetwork, matrix);
		this.readData = this.nextReadData = VectorValue.ofUnsigned(matrix.getColumnCount(), 0);
		this.readDataSignal = new ReadDataSignal(getRealm());
	}

	public RtlSynchronousRam(RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		super(clockNetwork, rowCount, columnCount);
		this.readData = this.nextReadData = VectorValue.ofUnsigned(getMatrix().getColumnCount(), 0);
		this.readDataSignal = new ReadDataSignal(getRealm());
	}

	public RtlVectorSignal getReadDataSignal() {
		return readDataSignal;
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
				MemoryImplementationUtil.generateMif(context.getAuxiliaryFileFactory(), memoryName + ".mif", getMatrix());
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(getAddressSignal(), VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
				consumer.consumeSignalUsage(getWriteEnableSignal(), VerilogExpressionNesting.ALL);
				consumer.consumeSignalUsage(getWriteDataSignal(), VerilogExpressionNesting.ALL);
			}

			@Override
			public void analyzePins(PinConsumer consumer) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				Matrix matrix = getMatrix();

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
				out.print(getAddressSignal());
				out.println("];");

				//
				out.print("\tif (");
				out.print(getWriteEnableSignal());
				out.println(") begin");

				//
				out.print("\t\t" + memoryName + "[");
				out.print(getAddressSignal());
				out.print("] <= ");
				out.print(getWriteDataSignal());
				out.println(";");

				//
				out.println("\tend");

				//
				out.println("end");

			}

		};
	}

	//
	//
	//

	public final class ReadDataSignal extends RtlItem implements RtlVectorSignal {

		public ReadDataSignal(RtlRealm realm) {
			super(realm);
		}

		@Override
		public int getWidth() {
			return getMatrix().getColumnCount();
		}

		@Override
		public VectorValue getValue() {
			return readData;
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		@Override
		public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			// Usage of the input signals gets reported in our VerilogContribution, which is correct because
			// the memory-reading always-block gets generated regardless of whether the read data signal is
			// actually used anywhere. As a consequence, there is nothing to do here.
		}

		@Override
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			// this signal should be declared without assignment, so this method should never be called
			throw new UnsupportedOperationException();
		}

	}

}
