package name.martingeisse.esdk.mahdl_test;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableBitSignal;
import org.junit.Test;

/**
 *
 */
public class NotTest {

	@Test
	public void testNot() {
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlSimulatedSettableBitSignal input = new RtlSimulatedSettableBitSignal(realm);
		Rtlnot
	}

}
