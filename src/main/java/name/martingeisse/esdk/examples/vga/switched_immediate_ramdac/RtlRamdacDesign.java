/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.switched_immediate_ramdac;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitSignal;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlConditionChain;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlConditionalVectorOperation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.examples.vga.VgaTimer;

/**
 *
 */
public class RtlRamdacDesign extends Design {

	public static final int WIDTH_BITS = 7;
	public static final int HEIGHT_BITS = 7;
	public static final int ROW_COPIER_LAST_COLUMN = 127; // later: 639 or 511

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final TestRenderer testRenderer;
	private final VgaTimer vgaTimer;

	private final RtlMemory framebuffer;
	private final RtlSynchronousMemoryPort framebufferPort;

	private final RtlProceduralVectorSignal rowCopierFramebufferRowIndex;
	private final RtlProceduralVectorSignal rowCopierFramebufferColumnIndex;
	private final RtlProceduralBitSignal rowCopierReadActive;
	private final RtlProceduralBitSignal rowCopierDataAvailable;
	private final RtlProceduralVectorSignal rowCopierWriteAddress;

	private final RtlMemory rowBuffer;
	private final RtlSynchronousMemoryPort rowBufferWritePort;
	private final RtlSynchronousMemoryPort rowBufferReadPort;

	private final RtlOutputPin r;
	private final RtlOutputPin g;
	private final RtlOutputPin b;
	private final RtlOutputPin hsync;
	private final RtlOutputPin vsync;

	public RtlRamdacDesign() {

		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(clockPin(realm));
		testRenderer = new TestRenderer(realm, clock, WIDTH_BITS, HEIGHT_BITS);
		vgaTimer = new VgaTimer(clock);

		RtlClockedBlock rowCopier = new RtlClockedBlock(clock);
		rowCopierReadActive = rowCopier.createBit();
		rowCopier.getInitializerStatements().assign(rowCopierReadActive, false);
		rowCopierDataAvailable = rowCopier.createBit();
		rowCopier.getInitializerStatements().assign(rowCopierDataAvailable, false);
		rowCopierFramebufferRowIndex = rowCopier.createVector(WIDTH_BITS);
		rowCopierFramebufferColumnIndex = rowCopier.createVector(HEIGHT_BITS);
		rowCopierWriteAddress = rowCopier.createVector(WIDTH_BITS);
		//
		{
			RtlConditionChain chain = rowCopier.getStatements().conditionChain();

			// start frame at end (rising edge) of vsync pulse
			RtlStatementSequence startFrame = chain.when(RtlBuilder.synchronousRisingEdge(clock, vgaTimer.getVsync()));
			startFrame.assignUnsigned(rowCopierFramebufferRowIndex, 0);
			startFrame.assignUnsigned(rowCopierFramebufferColumnIndex, 0);
			startFrame.assignUnsigned(rowCopierWriteAddress, 0);
			startFrame.assign(rowCopierReadActive, true);

			// go to next row and prefetch it at the end of the current row (rising edge of blank signal)
			// will fetch one row too much at the end of the frame but we accept that
			RtlStatementSequence prefetchNextRow = chain.when(RtlBuilder.synchronousRisingEdge(clock, vgaTimer.getBlank()));
			prefetchNextRow.assign(rowCopierFramebufferRowIndex, rowCopierFramebufferRowIndex.add(1));
			prefetchNextRow.assignUnsigned(rowCopierFramebufferColumnIndex, 0);
			prefetchNextRow.assignUnsigned(rowCopierWriteAddress, 0);
			prefetchNextRow.assign(rowCopierReadActive, true);

			// during row: increment column index (currently one read per cycle) and detect end of row
			RtlStatementSequence duringRow = chain.otherwise();
			duringRow.assign(rowCopierFramebufferColumnIndex, rowCopierFramebufferColumnIndex.add(1));
			duringRow.when(rowCopierDataAvailable).getThenBranch().assign(rowCopierWriteAddress, rowCopierWriteAddress.add(1));
			duringRow.when(rowCopierFramebufferRowIndex.compareEqual(ROW_COPIER_LAST_COLUMN)).getThenBranch().assign(rowCopierReadActive, false);
		}
		rowCopier.getStatements().assign(rowCopierDataAvailable, rowCopierReadActive); // currently reading in 1 cycle

		// Note: rows and columns of the frame are not rows and columns of the RAM. Instead, the RAM
		// has one row per pixel and 3 columns (bits) for the 3 color channels.
		framebuffer = new RtlMemory(getRealm(), 1 << (WIDTH_BITS + HEIGHT_BITS), 3);
		framebufferPort = framebuffer.createSynchronousPort(clock,
			RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
		framebufferPort.setWriteEnableSignal(testRenderer.getFramebufferWriteStrobe());
		framebufferPort.setWriteDataSignal(testRenderer.getFramebufferWriteData().select(2, 0));
		framebufferPort.setAddressSignal(new RtlConditionalVectorOperation(getRealm(),
			testRenderer.getFramebufferRamdacSwitch(),
			new RtlConcatenation(getRealm(), rowCopierFramebufferRowIndex, rowCopierFramebufferColumnIndex),
			testRenderer.getFramebufferWriteAddress()));

		rowBuffer = new RtlMemory(getRealm(), 1 << WIDTH_BITS, 3);
		rowBufferWritePort = rowBuffer.createSynchronousPort(clock, RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS);
		rowBufferWritePort.setWriteEnableSignal(rowCopierDataAvailable);
		rowBufferWritePort.setAddressSignal(rowCopierWriteAddress);
		rowBufferWritePort.setWriteDataSignal(framebufferPort.getReadDataSignal());
		rowBufferReadPort = rowBuffer.createSynchronousPort(clock, RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS);
		rowBufferReadPort.setAddressSignal(vgaTimer.getX().select(7, 1));

		// VGA interface
		RtlVectorSignal dacReadData = rowBufferReadPort.getReadDataSignal();
		RtlBitSignal blank = vgaTimer.getBlank().or(vgaTimer.getX().select(8)).or(vgaTimer.getX().select(9))
			.or(vgaTimer.getY().select(8)).or(vgaTimer.getY().select(9));
		RtlBitSignal active = blank.not();
		r = vgaPin(realm, "H14", active.and(dacReadData.select(2)));
		g = vgaPin(realm, "H15", active.and(dacReadData.select(1)));
		b = vgaPin(realm, "G15", active.and(dacReadData.select(0)));
		hsync = vgaPin(realm, "F15", vgaTimer.getHsync());
		vsync = vgaPin(realm, "F14", vgaTimer.getVsync());

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public RtlOutputPin getR() {
		return r;
	}

	public RtlOutputPin getG() {
		return g;
	}

	public RtlOutputPin getB() {
		return b;
	}

	public RtlOutputPin getHsync() {
		return hsync;
	}

	public RtlOutputPin getVsync() {
		return vsync;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId("C9");
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlOutputPin vgaPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setDrive(8);
		configuration.setSlew(XilinxPinConfiguration.Slew.FAST);
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(new XilinxPinConfiguration());
		pin.setOutputSignal(outputSignal);
		return pin;
	}

}
