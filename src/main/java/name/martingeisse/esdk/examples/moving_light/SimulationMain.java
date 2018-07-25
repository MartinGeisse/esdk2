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

		public SimulationController(MovingLightDesign design) {
			super(design);
			this.design = design;
		}

		@Override
		protected void initializeSimulation() {
			fire(this::callback, 0);
		}

		private void callback() {
			design.getClk().fireClockEdge();
			System.out.println(counter + ": " + design.getLeds().getValue());
			counter++;
			if (counter == 5) {
				getDesign().stopSimulation();
			} else {
				fire(this::callback, 1000);
			}
		}

	}

}
