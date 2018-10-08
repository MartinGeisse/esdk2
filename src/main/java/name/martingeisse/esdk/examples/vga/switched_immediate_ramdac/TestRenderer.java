package name.martingeisse.esdk.examples.vga.switched_immediate_ramdac;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementBuilder;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

/**
 *
 */
public final class TestRenderer extends RtlItem {

	private final PicoblazeRtlWithAssociatedProgram cpu;
	private final RtlProceduralVectorSignal columnRegister;
	private final RtlProceduralVectorSignal rowRegister;
	private final RtlBitSignal writeStrobe;
	private final RtlVectorSignal writeAddress;
	private final RtlVectorSignal writeData;

	public TestRenderer(RtlRealm realm, RtlClockNetwork clock, int widthBits, int heightBits) {
		super(realm);
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, getClass());

		{
			RtlClockedBlock block = new RtlClockedBlock(clock);
			columnRegister = block.createVector(widthBits);
			rowRegister = block.createVector(heightBits);
			block.getInitializerStatements().assign(columnRegister, VectorValue.ofUnsigned(widthBits, 0));
			block.getInitializerStatements().assign(rowRegister, VectorValue.ofUnsigned(heightBits, 0));

			RtlStatementBuilder builder = block.getStatements().builder();
			builder.when(cpu.getWriteStrobe());
			{
				builder.when(cpu.getPortAddress().select(7));
				{
					builder.when(cpu.getPortAddress().select(6));
					builder.assign(columnRegister, columnRegister.add(1));
					builder.otherwise();
					builder.assign(columnRegister, columnRegister.subtract(1));
					builder.endWhen();
				}
				builder.otherwise();
				{
					builder.when(cpu.getPortAddress().select(6));
					if (widthBits > 8) {
						builder.assign(columnRegister, new RtlConcatenation(realm, columnRegister.select(widthBits - 9, 0), cpu.getOutputData()));
					} else {
						builder.assign(columnRegister, cpu.getOutputData().select(widthBits - 1, 0));
					}
					builder.endWhen();
				}
				builder.endWhen();
				builder.when(cpu.getPortAddress().select(5));
				{
					builder.when(cpu.getPortAddress().select(4));
					builder.assign(rowRegister, rowRegister.add(1));
					builder.otherwise();
					builder.assign(rowRegister, rowRegister.subtract(1));
					builder.endWhen();
				}
				builder.otherwise();
				{
					builder.when(cpu.getPortAddress().select(4));
					if (heightBits > 8) {
						builder.assign(rowRegister, new RtlConcatenation(realm, rowRegister.select(heightBits - 9, 0), cpu.getOutputData()));
					} else {
						builder.assign(rowRegister, cpu.getOutputData().select(heightBits - 1, 0));
					}
					builder.endWhen();
				}
				builder.endWhen();
			}
			builder.endWhen();
		}

		cpu.setPortInputDataSignal(new RtlVectorConstant(realm, VectorValue.ofUnsigned(8, 0)));
		cpu.setResetSignal(new RtlBitConstant(realm, false));

		writeStrobe = cpu.getWriteStrobe().and(cpu.getPortAddress().select(0));
		writeAddress = new RtlConcatenation(getRealm(), rowRegister, columnRegister);
		writeData = cpu.getOutputData().select(2, 0);
	}

	public RtlBitSignal getWriteStrobe() {
		return writeStrobe;
	}

	public RtlVectorSignal getWriteAddress() {
		return writeAddress;
	}

	public RtlVectorSignal getWriteData() {
		return writeData;
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
