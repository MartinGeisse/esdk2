package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlMemory extends RtlItem {

	private final Matrix matrix;
	private final List<RtlMemoryPort> ports;
	private final RtlSignal memorySignal;

	public RtlMemory(RtlRealm realm, Matrix matrix) {
		super(realm);
		this.matrix = matrix;
		this.ports = new ArrayList<>();
		this.memorySignal = new RtlSignal() {

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
				throw newSynthesisNotSupportedException();
			}

			@Override
			public RtlItem getRtlItem() {
				return RtlMemory.this;
			}

		};
	}

	public RtlMemory(RtlRealm realm, int rowCount, int columnCount) {
		this(realm, new Matrix(rowCount, columnCount));
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public Iterable<RtlMemoryPort> getPorts() {
		return ports;
	}

	public RtlAsynchronousMemoryReadPort createAsynchronousReadPort() {
		RtlAsynchronousMemoryReadPort port = new RtlAsynchronousMemoryReadPort(this);
		ports.add(port);
		return port;
	}

	public RtlSynchronousMemoryPort createSynchronousPort(RtlClockNetwork clock,
														  RtlSynchronousMemoryPort.ReadSupport readSupport) {
		return createSynchronousPort(clock, readSupport, RtlSynchronousMemoryPort.WriteSupport.NONE,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
	}

	public RtlSynchronousMemoryPort createSynchronousPort(RtlClockNetwork clock,
														  RtlSynchronousMemoryPort.WriteSupport writeSupport) {
		return createSynchronousPort(clock, RtlSynchronousMemoryPort.ReadSupport.NONE, writeSupport,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
	}

	public RtlSynchronousMemoryPort createSynchronousPort(RtlClockNetwork clock,
														  RtlSynchronousMemoryPort.ReadSupport readSupport,
														  RtlSynchronousMemoryPort.WriteSupport writeSupport,
														  RtlSynchronousMemoryPort.ReadWriteInteractionMode readWriteInteractionMode) {
		RtlSynchronousMemoryPort port = new RtlSynchronousMemoryPort(clock, this, readSupport, writeSupport, readWriteInteractionMode);
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
				for (RtlMemoryPort port : ports) {
					port.prepareSynthesis(context);
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				for (RtlMemoryPort port : ports) {
					port.analyzeSignalUsage(consumer);
				}
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
				Matrix matrix = getMatrix();
				out.println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
					(matrix.getRowCount() - 1) + ":0];");
				for (RtlMemoryPort port : ports) {
					port.printDeclarations(out);
				}
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				Matrix matrix = getMatrix();
				out.println("initial $readmemh(\"" + memoryName + ".mif\", " + memoryName + ", 0, " +
					(matrix.getRowCount() - 1) + ");\n");
				for (RtlMemoryPort port : ports) {
					port.printImplementation(out);
				}
			}

		};
	}

}
