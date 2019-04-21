package name.martingeisse.esdk.examples.vga;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitSignal;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlConditionChainStatement;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public class VgaTimer {

	private final RtlProceduralVectorSignal x;
	private final RtlProceduralVectorSignal y;
	private final RtlProceduralBitSignal hsync;
	private final RtlProceduralBitSignal vsync;
	private final RtlBitSignal blank;

	public VgaTimer(RtlClockNetwork clock) {

		RtlClockedBlock block = new RtlClockedBlock(clock);
		x = block.createVector(10);
		y = block.createVector(10);
		hsync = block.createBit();
		vsync = block.createBit();
		RtlProceduralBitSignal p = block.createBit();
		RtlProceduralBitSignal xblank = block.createBit();
		RtlProceduralBitSignal yblank = block.createBit();

		RtlStatementSequence initializer = block.getInitializerStatements();
		initializer.assign(p, false);
		initializer.assignUnsigned(x, 0);
		initializer.assignUnsigned(y, 0);
		initializer.assign(hsync, true);
		initializer.assign(vsync, true);
		initializer.assign(xblank, false);
		initializer.assign(yblank, false);

		RtlStatementSequence statements = block.getStatements();
		statements.assign(p, p.not());
		RtlStatementSequence pixelFinished = statements.when(p).getThenBranch();
		RtlWhenStatement whenRowFinished = pixelFinished.when(x.compareEqual(799));
		{
			RtlStatementSequence rowFinished = whenRowFinished.getThenBranch();
			rowFinished.assignUnsigned(x, 0);
			rowFinished.assign(xblank, false);
			RtlWhenStatement whenFrameFinished = rowFinished.when(y.compareEqual(524));
			{
				RtlStatementSequence frameFinished = whenFrameFinished.getThenBranch();
				frameFinished.assignUnsigned(y, 0);
				frameFinished.assign(yblank, false);
			}
			{
				RtlStatementSequence frameNotFinished = whenFrameFinished.getOtherwiseBranch();
				RtlConditionChainStatement chain = frameNotFinished.conditionChain();
				chain.when(y.compareEqual(479)).assign(yblank, true);
				chain.when(y.compareEqual(489)).assign(vsync, false);
				chain.when(y.compareEqual(491)).assign(vsync, true);
				frameNotFinished.assign(y, y.add(1));
			}
		}
		{
			RtlStatementSequence rowNotFinished = whenRowFinished.getOtherwiseBranch();
			RtlConditionChainStatement chain = rowNotFinished.conditionChain();
			chain.when(x.compareEqual(639)).assign(xblank, true);
			chain.when(x.compareEqual(655)).assign(hsync, false);
			chain.when(x.compareEqual(751)).assign(hsync, true);
			rowNotFinished.assign(x, x.add(1));
		}

		blank = xblank.or(yblank);
	}

	public RtlVectorSignal getX() {
		return x;
	}

	public RtlVectorSignal getY() {
		return y;
	}

	public RtlBitSignal getHsync() {
		return hsync;
	}

	public RtlBitSignal getVsync() {
		return vsync;
	}

	public RtlBitSignal getBlank() {
		return blank;
	}

}
