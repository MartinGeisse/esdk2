package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitSignal;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementBuilder;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.examples.vga.test_renderer.display.FramebufferDisplay;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

import java.util.function.Predicate;

/**
 *
 */
public final class TestRendererDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final PicoblazeRtlWithAssociatedProgram cpu;
	private final RtlBitSignalConnector displayReady;
	private final RtlProceduralVectorSignal columnRegister;
	private final RtlProceduralVectorSignal rowRegister;

	public TestRendererDesign(int widthBits, int heightBits) {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, getClass());
		displayReady = new RtlBitSignalConnector(realm);

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

		cpu.setPortInputDataSignal(new RtlConcatenation(realm,
			new RtlVectorConstant(realm, VectorValue.ofUnsigned(7, 0)),
			displayReady));
		cpu.setResetSignal(new RtlBitConstant(realm, false));

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public void connectDisplay(FramebufferDisplay display) {
		display.setWriteAddressSignal(new RtlConcatenation(realm, rowRegister, columnRegister));
		display.setWriteStrobeSignal(cpu.getWriteStrobe().and(cpu.getPortAddress().select(0)));
		display.setWriteDataSignal(cpu.getOutputData().select(2, 0));
		displayReady.setConnected(display.getReadySignal());
	}

}
