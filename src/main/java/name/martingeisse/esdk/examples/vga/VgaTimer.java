package name.martingeisse.esdk.examples.vga;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;

/**
 *
 */
public class VgaTimer {

	public VgaTimer(RtlClockNetwork clock) {
		RtlClockedBlock block = new RtlClockedBlock(clock.getRealm(), clock);

	}

}
