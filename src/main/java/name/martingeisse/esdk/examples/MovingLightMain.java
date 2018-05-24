package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.rtl.RtlClockNetwork;
import name.martingeisse.esdk.rtl.RtlClockedBlock;
import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlProceduralVectorSignal;

/**
 *
 */
public class MovingLightMain {

	public static void main(String[] args) {

		RtlDesign design = new RtlDesign();
		RtlClockNetwork clk = design.createClockNetwork();

		RtlClockedBlock block = clk.createBlock();
		RtlProceduralVectorSignal prescaler = block.createVector(24);
		block.getInitializerStatements().assignUnsigned(prescaler, 0);


	}

}
