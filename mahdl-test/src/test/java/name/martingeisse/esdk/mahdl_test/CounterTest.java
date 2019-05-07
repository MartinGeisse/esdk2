package name.martingeisse.esdk.mahdl_test;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.library.util.ClockStepper;
import org.junit.Assert;
import org.junit.Test;
import tests.Counter;

/**
 *
 */
public class CounterTest {

	@Test
	public void testCounter() {
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = new RtlClockNetwork(realm);
		Counter counter = new Counter(realm, clock);
		ClockStepper stepper = new ClockStepper(clock, 10);
		design.prepareSimulation();

		for (int i = 0; i < 8; i++) {
			Assert.assertEquals(i, counter.getOutput().getValue().getAsUnsignedInt());
			stepper.step();
		}
		Assert.assertEquals(0, counter.getOutput().getValue().getAsUnsignedInt());
		stepper.step();
		Assert.assertEquals(1, counter.getOutput().getValue().getAsUnsignedInt());
	}

}
