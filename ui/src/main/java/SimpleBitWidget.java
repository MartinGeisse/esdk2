import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class SimpleBitWidget implements VisualizerWidget {

	private final RtlBitSignal signal;
	private boolean value;

	public SimpleBitWidget(RtlBitSignal signal) {
		this.signal = signal;
	}

	@Override
	public int getWidthInCells() {
		return 1;
	}

	@Override
	public int getHeightInCells() {
		return 1;
	}

	@Override
	public void sampleOutputs() {
		value = signal.getValue();
	}

	@Override
	public JComponent createSwingComponent() {
		return new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(value ? Color.WHITE : Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
	}

}
