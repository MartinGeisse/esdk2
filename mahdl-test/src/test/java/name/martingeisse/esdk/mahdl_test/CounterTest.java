package name.martingeisse.esdk.mahdl_test;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
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
		RtlClockNetwork clk = new RtlClockNetwork(realm);
		Counter counter = new Counter(realm, clk);
		new Item(design) {

			private int timer = 0;

			@Override
			protected void initializeSimulation() {
				fire(this::callback, 0);
			}

			private void callback() {
				System.out.println("* " + counter.getOutput().getValue());
				clk.simulateClockEdge();
				timer++;
				if (timer == 10) {
					getDesign().stopSimulation();
				} else {
					fire(this::callback, 10);
				}
			}

		};
		design.simulate();
	}

}
