package name.martingeisse.esdk.core.rtl.memory.multiport;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class SynchronousPort extends RtlClockedItem implements MemoryPort {

	private final RtlMultiportMemory memory;
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

	SynchronousPort(RtlClockNetwork clock, RtlMultiportMemory memory,
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
				readDataSignal = new SynchronousReadDataSignal(getRealm());
				break;

			default:
				readDataSignal = null;
				break;

		}
	}

	public RtlMultiportMemory getMemory() {
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
			out.print(memory.getMemorySignal(), VerilogExpressionNesting.ALL);
			out.print('[');
			out.print(addressSignal, VerilogExpressionNesting.ALL);
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
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			// TODO
//			out.print(memory.getMemorySignal(), VerilogExpressionNesting.ALL);
//			out.print('[');
//			out.print(addressSignal, VerilogExpressionNesting.ALL);
//			out.print(']');
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		validate();
	}

	@Override
	public void computeNextState() {
		sampledClockEnable = clockEnableSignal == null || clockEnableSignal.getValue();
		sampledWriteEnable = writeEnableSignal == null || writeEnableSignal.getValue();
		sampledAddress = addressSignal == null ? null : addressSignal.getValue();
		sampledWriteData = writeDataSignal == null ? null : writeDataSignal.getValue();
	}

	@Override
	public void updateState() {
		TODO
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void validate() {
		if (addressSignal == null) {
			throw new IllegalStateException("no address signal in synchronous memory port");
		}
		if (writeSupport != WriteSupport.NONE && writeDataSignal == null) {
		}
		if (writeSupport == WriteSupport.NONE && writeEnableSignal != null) {
			throw new IllegalStateException("synchronous memory port with write enable signal but no write support");
		}
		if (writeSupport == WriteSupport.NONE && writeDataSignal != null) {
			throw new IllegalStateException("synchronous memory port with write data signal but no write support");
		}
	}

}
