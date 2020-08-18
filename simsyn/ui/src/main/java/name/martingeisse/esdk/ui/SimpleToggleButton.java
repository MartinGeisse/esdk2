package name.martingeisse.esdk.ui;

import name.martingeisse.esdk.core.rtl.RtlRealm;

import javax.swing.*;

/**
 *
 */
public class SimpleToggleButton extends SimpleButton {

	public SimpleToggleButton(RtlRealm realm, String label) {
		super(realm, label);
	}

	public SimpleToggleButton(RtlRealm realm, int widthInCells, String label) {
		super(realm, widthInCells, label);
	}

	public SimpleToggleButton(RtlRealm realm, int widthInCells, int heightInCells, String label) {
		super(realm, widthInCells, heightInCells, label);
	}

	@Override
	public JComponent createSwingComponent() {
		JToggleButton button = new JToggleButton(getLabel());
		button.addChangeListener(event -> {
			getSignal().setValue(button.getModel().isSelected());
		});
		return button;
	}

}
