package name.martingeisse.esdk.ui;

import javax.swing.*;

/**
 *
 */
public interface VisualizerWidget {
	int getWidthInCells();
	int getHeightInCells();
	JComponent createSwingComponent();
	void sampleOutputs();
}
