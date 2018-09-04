package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.examples.vga.test_renderer.FramebufferDisplay;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class TestRendererDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final FramebufferDisplay display;
	private final PicoblazeRtlWithAssociatedProgram cpu;

	public TestRendererDesign(BufferedImage framebuffer, int widthBits) {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		display = new FramebufferDisplay(clock, framebuffer, widthBits);
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, getClass());

		RtlProceduralVectorSignal rowRegister;
		RtlProceduralVectorSignal columnRegister;
		{
			RtlClockedBlock block = new RtlClockedBlock(clock);
			rowRegister = block.createVector(10);
			columnRegister = block.createVector(9);
			block.getInitializerStatements().assign(rowRegister, VectorValue.ofUnsigned(10, 0));
			block.getInitializerStatements().assign(columnRegister, VectorValue.ofUnsigned(9, 0));
			RtlStatementSequence onWrite = block.getStatements().when(cpu.getWriteStrobe()).getThenBranch();

			{
				RtlWhenStatement when7 = onWrite.when(cpu.getPortAddress().select(7));
				RtlWhenStatement when7When6 = when7.getThenBranch().when(cpu.getPortAddress().select(6));
				RtlWhenStatement whenNot7When6 = when7.getOtherwiseBranch().when(cpu.getPortAddress().select(6));
				when7When6.getThenBranch().assign(rowRegister, rowRegister.add(1));
				when7When6.getOtherwiseBranch().assign(rowRegister, rowRegister.subtract(1));
				whenNot7When6.getThenBranch().assign(rowRegister,
					new RtlConcatenation(realm, rowRegister.select(1, 0), cpu.getOutputData()));
			}

			{
				RtlWhenStatement when5 = onWrite.when(cpu.getPortAddress().select(5));
				RtlWhenStatement when5When4 = when5.getThenBranch().when(cpu.getPortAddress().select(4));
				RtlWhenStatement whenNot5When4 = when5.getOtherwiseBranch().when(cpu.getPortAddress().select(4));
				when5When4.getThenBranch().assign(columnRegister, columnRegister.add(1));
				when5When4.getOtherwiseBranch().assign(columnRegister, columnRegister.subtract(1));
				whenNot5When4.getThenBranch().assign(columnRegister,
					new RtlConcatenation(realm, columnRegister.select(0), cpu.getOutputData()));
			}

			block.getStatements().when(cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)));
		}

		cpu.setPortInputDataSignal(new RtlVectorConstant(realm, VectorValue.ofUnsigned(8, 0)));
		cpu.setResetSignal(new RtlBitConstant(realm, false));
		display.setWriteAddressSignal(new RtlConcatenation(realm, columnRegister, rowRegister));
		display.setWriteStrobeSignal(cpu.getWriteStrobe().and(cpu.getPortAddress().select(0)));
		display.setWriteDataSignal(cpu.getOutputData().select(2, 0));

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public FramebufferDisplay getDisplay() {
		return display;
	}

}
