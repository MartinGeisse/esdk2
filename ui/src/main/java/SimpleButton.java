import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableBitSignal;

import javax.swing.*;

/**
 *
 */
public class SimpleButton implements VisualizerWidget {

	private final int widthInCells;
	private final int heightInCells;
	private final String label;
	private final RtlSimulatedSettableBitSignal signal;

	public SimpleButton(RtlRealm realm, String label) {
		this(realm, 5, label);
	}

	public SimpleButton(RtlRealm realm, int widthInCells, String label) {
		this(realm, widthInCells, 1, label);
	}

	public SimpleButton(RtlRealm realm, int widthInCells, int heightInCells, String label) {
		this.widthInCells = widthInCells;
		this.heightInCells = heightInCells;
		this.label = label;
		this.signal = new RtlSimulatedSettableBitSignal(realm);
	}

	public RtlSimulatedSettableBitSignal getSignal() {
		return signal;
	}

	@Override
	public int getWidthInCells() {
		return widthInCells;
	}

	@Override
	public int getHeightInCells() {
		return heightInCells;
	}

	@Override
	public JComponent createSwingComponent() {
		JButton button = new JButton(label);
		button.addChangeListener(event -> {
			signal.setValue(button.getModel().isPressed());
		});
		return button;
	}

	@Override
	public void sampleOutputs() {
	}

}
