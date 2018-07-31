package name.martingeisse.esdk.examples.moving_light;

import name.martingeisse.esdk.core.model.Item;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
		MovingLightDesign design = new MovingLightDesign();
		new SimulationController(design);
		design.simulate();
	}

	private static class SimulationController extends Item {

		private final MovingLightDesign design;
		private int counter = 0;
		private boolean slideSwitch = false;

		public SimulationController(MovingLightDesign design) {
			super(design);
			this.design = design;
		}

		@Override
		protected void initializeSimulation() {
			fire(this::callback, 0);
		}

		private void callback() {
			design.getClk().simulateClockEdge();
			if (counter % (2 * 1024 * 1024) == 0) {
				System.out.println(counter + ": " + design.getLeds().getValue());
			}
			counter++;
			if (counter == 150_000_000) {
				slideSwitch = !slideSwitch;
				design.getSlideSwitch().getSettableBitSignal().setValue(slideSwitch);
			}
			if (counter == 1000 * 1024 * 1024) {
				getDesign().stopSimulation();
			} else {
				fire(this::callback, 10);
			}
		}

	}

}
