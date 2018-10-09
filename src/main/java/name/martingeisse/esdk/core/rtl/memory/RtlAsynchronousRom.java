/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlAsynchronousRom extends RtlItem {

	private final Matrix matrix;
	private RtlVectorSignal addressSignal;
	private final RtlVectorSignal readDataSignal;

	public RtlAsynchronousRom(RtlRealm realm, Matrix matrix) {
		super(realm);
		this.matrix = matrix;
		this.readDataSignal = new ReadDataSignal(getRealm());
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
		return readDataSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(addressSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);

			}

			@Override
			public void printImplementation(VerilogWriter out) {

				// TODO

			}

		};
	}

//	// TODO not called yet
//	public void printImplementation(VerilogWriter out) {
//		String memoryName = out.newMemoryName();
//		String mifName = memoryName + ".mif";
//
//		// memory
//		out.getOut().println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
//			(matrix.getRowCount() - 1) + ":0];");
//
//		// initialization
//		out.getOut().println("initial $readmemh(\"" + mifName + "\", " + memoryName + ", 0, " + (matrix.getRowCount() - 1) + ");\n");
//
//		// note: we use an always-block to read the memory because otherwise we would somehow have to share the name
//		// assigned to the memory with the ReadDataSignal during verilog generation. It just simplifies the generation
//		// code -- we could have implemented the ReadDataSignal as a custom signal with implementation "myMem[address]".
//		out.getOut().print("always @(*) begin");
//		out.getOut().print('\t');
//		out.printExpression(readDataSignal);
//		out.getOut().print(" <= " + memoryName + "[");
//		out.printExpression(addressSignal);
//		out.getOut().println("];");
//		out.getOut().println("end");
//
//		try {
//			out.generateMif(mifName, matrix);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public Iterable<? extends RtlSignal> getSignalsThatRequireDeclarationInVerilog() {
//		return Arrays.asList(readDataSignal);
//	}

	//
	//
	//

	public final class ReadDataSignal extends RtlItem implements RtlVectorSignal {

		public ReadDataSignal(RtlRealm realm) {
			super(realm);
		}

		@Override
		public int getWidth() {
			return matrix.getColumnCount();
		}

		@Override
		public VectorValue getValue() {
			return matrix.getRow(addressSignal.getValue().getAsUnsignedInt());
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		// TODO ???
//		@Override
//		public void analyzeSignalUsage(SignalUsageConsumer consumer) {
//		}

		@Override
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			// this signal should be declared without assignment, so this method should never be called
			throw new UnsupportedOperationException();
		}

	}

}
