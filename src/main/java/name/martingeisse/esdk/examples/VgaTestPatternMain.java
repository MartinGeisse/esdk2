package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.rtl.RtlClockNetwork;
import name.martingeisse.esdk.rtl.RtlDesign;

/**
 *
 */
public class VgaTestPatternMain {

	public static void main(String[] args) {

		RtlDesign design = new RtlDesign();
		RtlClockNetwork clk = design.createClockNetwork();

	}

}
