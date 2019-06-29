package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.model.validation.ValidationContext;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlSynchronousMemoryPort extends RtlClockedItem implements RtlMemoryPort {

	private final RtlMemory memory;
	private final ReadSupport readSupport;
	private final WriteSupport writeSupport;
	private final ReadWriteInteractionMode readWriteInteractionMode;

	private RtlBitSignal clockEnableSignal;
	private RtlBitSignal writeEnableSignal;
	private RtlVectorSignal addressSignal;
	private RtlVectorSignal writeDataSignal;

	private boolean sampledClockEnable;
	private boolean sampledWriteEnable;
	private VectorValue sampledAddress;
	private VectorValue sampledWriteData;

	private VectorValue synchronousReadData;
	private final RtlVectorSignal readDataSignal;

	RtlSynchronousMemoryPort(RtlClockNetwork clock, RtlMemory memory,
							 ReadSupport readSupport, WriteSupport writeSupport,
							 ReadWriteInteractionMode readWriteInteractionMode) {
		super(clock);
		if (readSupport == null) {
			throw new IllegalArgumentException("readSupport is null");
		}
		if (writeSupport == null) {
			throw new IllegalArgumentException("writeSupport is null");
		}
		if (readWriteInteractionMode == null) {
			throw new IllegalArgumentException("readWriteInteractionMode is null");
		}
		this.memory = memory;
		this.readSupport = readSupport;
		this.writeSupport = writeSupport;
		this.readWriteInteractionMode = readWriteInteractionMode;
		switch (readSupport) {

			case ASYNCHRONOUS:
				readDataSignal = new AsynchronousReadDataSignal(getRealm());
				break;

			case SYNCHRONOUS:
				synchronousReadData = VectorValue.of(memory.getMatrix().getColumnCount(), 0);
				readDataSignal = new SynchronousReadDataSignal(getRealm());
				break;

			default:
				readDataSignal = null;
				break;

		}
	}

	public RtlMemory getMemory() {
		return memory;
	}

	public ReadSupport getReadSupport() {
		return readSupport;
	}

	public WriteSupport getWriteSupport() {
		return writeSupport;
	}

	public ReadWriteInteractionMode getReadWriteInteractionMode() {
		return readWriteInteractionMode;
	}

	public RtlVectorSignal getReadDataSignal() {
		return readDataSignal;
	}

	public RtlBitSignal getClockEnableSignal() {
		return clockEnableSignal;
	}

	public void setClockEnableSignal(RtlBitSignal clockEnableSignal) {
		this.clockEnableSignal = clockEnableSignal;
	}

	public RtlBitSignal getWriteEnableSignal() {
		return writeEnableSignal;
	}

	public void setWriteEnableSignal(RtlBitSignal writeEnableSignal) {
		this.writeEnableSignal = writeEnableSignal;
	}

	public RtlVectorSignal getAddressSignal() {
		return addressSignal;
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		this.addressSignal = addressSignal;
	}

	public RtlVectorSignal getWriteDataSignal() {
		return writeDataSignal;
	}

	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		this.writeDataSignal = writeDataSignal;
	}

	public enum ReadSupport {
		SYNCHRONOUS,
		ASYNCHRONOUS,
		NONE
	}

	public enum WriteSupport {
		SYNCHRONOUS,
		NONE
	}

	public enum ReadWriteInteractionMode {
		READ_FIRST,
		WRITE_FIRST,
		NO_READ // a.k.a. NO_CHANGE for Xilinx
	}

	// ----------------------------------------------------------------------------------------------------------------
	// helper signals
	// ----------------------------------------------------------------------------------------------------------------

	final class AsynchronousReadDataSignal extends RtlItem implements RtlVectorSignal {

		AsynchronousReadDataSignal(RtlRealm realm) {
			super(realm);
		}

		@Override
		public int getWidth() {
			return memory.getMatrix().getColumnCount();
		}

		@Override
		public VectorValue getValue() {
			return memory.getMatrix().getRow(addressSignal.getValue().getAsUnsignedInt());
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		@Override
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			out.printSignal(memory.getMemorySignal(), VerilogExpressionNesting.ALL);
			out.print('[');
			out.printSignal(addressSignal, VerilogExpressionNesting.ALL);
			out.print(']');
		}

	}

	final class SynchronousReadDataSignal extends RtlItem implements RtlVectorSignal {

		SynchronousReadDataSignal(RtlRealm realm) {
			super(realm);
		}

		@Override
		public int getWidth() {
			return memory.getMatrix().getColumnCount();
		}

		@Override
		public VectorValue getValue() {
			return synchronousReadData;
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		@Override
		public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		}

		@Override
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			// this signal must have been declared
			throw new UnsupportedOperationException("cannot write implementation expression for synchronous read data");
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void computeNextState() {
		sampledClockEnable = clockEnableSignal == null || clockEnableSignal.getValue();
		sampledWriteEnable = writeEnableSignal == null || writeEnableSignal.getValue();
		sampledAddress = addressSignal == null ? null : addressSignal.getValue();
		sampledWriteData = writeDataSignal == null ? null : writeDataSignal.getValue();
	}

	@Override
	public void updateState() {
		if (!sampledClockEnable) {
			// inactive
			return;
		}
		int rowIndex = sampledAddress.getAsUnsignedInt();
		VectorValue currentSynchronousReadResult = memory.getMatrix().getRow(rowIndex);
		if (writeSupport != WriteSupport.SYNCHRONOUS || !sampledWriteEnable) {
			// read
			synchronousReadData = currentSynchronousReadResult;
			return;
		}
		// write
		memory.getMatrix().setRow(rowIndex, sampledWriteData);
		switch (readWriteInteractionMode) {

			case NO_READ:
				break;

			case READ_FIRST:
				synchronousReadData = currentSynchronousReadResult;
				break;

			case WRITE_FIRST:
				synchronousReadData = sampledWriteData;
				break;

		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void validate(ValidationContext context) {
		if (addressSignal == null) {
			context.reportError("no address signal");
		}
		if (writeSupport != WriteSupport.NONE && writeDataSignal == null) {
			context.reportError("synchronous memory port with write support but no write data signal");
		}
		if (writeSupport == WriteSupport.NONE && writeEnableSignal != null) {
			context.reportError("synchronous memory port with write enable signal but no write support");
		}
		if (writeSupport == WriteSupport.NONE && writeDataSignal != null) {
			context.reportError("synchronous memory port with write data signal but no write support");
		}
	}

	@Override
	public void prepareSynthesis(SynthesisPreparationContext context) {
		if (readSupport == ReadSupport.SYNCHRONOUS) {
			context.declareSignal(readDataSignal, "s", VerilogSignalKind.REG, false);
		}
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(clockEnableSignal, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		consumer.consumeSignalUsage(writeEnableSignal, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		consumer.consumeSignalUsage(addressSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		consumer.consumeSignalUsage(writeDataSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

	@Override
	public void printDeclarations(VerilogWriter out) {
	}

	@Override
	public void printImplementation(VerilogWriter out) {

		// begin synchronous block
		out.indent();
		out.print("always @(posedge ");
		out.printSignal(getClockNetwork().getClockSignal());
		out.println(") begin");
		out.startIndentation();

		// read/write logic
		printBeginEnable(out, clockEnableSignal);
		if (readSupport == ReadSupport.SYNCHRONOUS && writeSupport == WriteSupport.SYNCHRONOUS && readWriteInteractionMode != ReadWriteInteractionMode.READ_FIRST) {
			if (writeEnableSignal == null) {
				throw new UnsupportedOperationException("printInteractingPorts() called without write enable -- " +
					"this is legal in principle but pointless, and therefore currently not supported");
			}
			printBeginEnable(out, writeEnableSignal);
			printWriteStatement(out);
			if (readWriteInteractionMode == ReadWriteInteractionMode.WRITE_FIRST) {
				printReadFromWriteStatement(out);
			}
			out.indent();
			out.println("end else begin");
			printReadStatement(out);
			printEndEnable(out, writeEnableSignal);
		} else {
			if (readSupport == ReadSupport.SYNCHRONOUS) {
				printReadStatement(out);
			}
			if (writeSupport == WriteSupport.SYNCHRONOUS) {
				printBeginEnable(out, writeEnableSignal);
				printWriteStatement(out);
				printEndEnable(out, writeEnableSignal);
			}
		}
		printEndEnable(out, clockEnableSignal);

		// end synchronous block
		out.endIndentation();
		out.indent();
		out.println("end");

	}

	private void printBeginEnable(VerilogWriter out, RtlBitSignal enable) {
		if (enable != null) {
			out.indent();
			out.print("if (");
			out.printSignal(enable);
			out.println(") begin");
			out.startIndentation();
		}
	}

	private void printEndEnable(VerilogWriter out, RtlBitSignal enable) {
		if (enable != null) {
			out.endIndentation();
			out.indent();
			out.println("end");
		}
	}

	private void printReadStatement(VerilogWriter out) {
		out.indent();
		out.printSignal(readDataSignal);
		out.print(" <= ");
		out.printSignal(memory.getMemorySignal());
		out.print('[');
		out.printSignal(getAddressSignal());
		out.println("];");
	}

	private void printWriteStatement(VerilogWriter out) {
		out.indent();
		out.printSignal(memory.getMemorySignal());
		out.print('[');
		out.printSignal(getAddressSignal());
		out.print("] <= ");
		out.printSignal(writeDataSignal);
		out.println(';');
	}

	private void printReadFromWriteStatement(VerilogWriter out) {
		out.indent();
		out.printSignal(readDataSignal);
		out.print(" <= ");
		out.printSignal(writeDataSignal);
		out.println("];");
	}

}
