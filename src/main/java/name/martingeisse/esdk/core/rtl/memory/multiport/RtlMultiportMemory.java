package name.martingeisse.esdk.core.rtl.memory.multiport;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlMultiportMemory extends RtlItem {

	private final Matrix matrix;
	private final List<MemoryPort> ports;
	private final RtlSignal memorySignal;

	public RtlMultiportMemory(RtlRealm realm, Matrix matrix) {
		super(realm);
		this.matrix = matrix;
		this.ports = new ArrayList<>();
		this.memorySignal = new RtlSignal() {

			@Override
			public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
				throw newSynthesisNotSupportedException();
			}

			@Override
			public RtlItem getRtlItem() {
				return RtlMultiportMemory.this;
			}

		};
	}

	public RtlMultiportMemory(RtlRealm realm, int rowCount, int columnCount) {
		this(realm, new Matrix(rowCount, columnCount));
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public Iterable<MemoryPort> getPorts() {
		return ports;
	}

	public AsynchronousReadPort createAsynchronousReadPort() {
		AsynchronousReadPort port = new AsynchronousReadPort(this);
		ports.add(port);
		return port;
	}

	public SynchronousPort createSynchronousPort(RtlClockNetwork clock,
												 SynchronousPort.ReadSupport readSupport,
												 SynchronousPort.WriteSupport writeSupport,
												 SynchronousPort.ReadWriteInteractionMode readWriteInteractionMode) {
		SynchronousPort port = new SynchronousPort(clock, readSupport, writeSupport, readWriteInteractionMode);
		ports.add(port);
		return port;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	RtlSignal getMemorySignal() {
		return memorySignal;
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			private String memoryName;

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				memoryName = context.declareSignal(memorySignal, "mem", true, null, false);
				MemoryImplementationUtil.generateMif(context.getAuxiliaryFileFactory(), memoryName + ".mif", getMatrix());
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				for (MemoryPort port : ports) {
					port.analyzeSignalUsage(consumer);
				}
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
				Matrix matrix = getMatrix();
				out.println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
					(matrix.getRowCount() - 1) + ":0];");
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				Matrix matrix = getMatrix();
				out.println("initial $readmemh(\"" + memoryName + ".mif\", " + memoryName + ", 0, " +
					(matrix.getRowCount() - 1) + ");\n");
			}

		};
	}

}
