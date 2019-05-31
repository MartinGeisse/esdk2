import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class VisualizerWindow {

	public static final int CELL_SIZE = 20;

	private final Design design;
	private final int widthInCells;
	private final int heightInCells;
	private final JFrame window;
	private final long refreshPeriod;
	private final List<VisualizerWidget> widgets = new ArrayList<>();

	public VisualizerWindow(Design design, int widthInCells, int heightInCells, long refreshPeriod) {
		this.design = design;
		this.widthInCells = widthInCells;
		this.heightInCells = heightInCells;
		this.refreshPeriod = refreshPeriod;

		window = new JFrame("Visualizer");
		window.setLayout(null);
		setSizeInCells(window, widthInCells, heightInCells);
		window.setPreferredSize(new Dimension(widthInCells * CELL_SIZE, heightInCells * CELL_SIZE));
		window.setSize(new Dimension(widthInCells * CELL_SIZE, heightInCells * CELL_SIZE));
		window.getContentPane().setBackground(new Color(0, 128, 0));
		new IntervalItem(design, 0, refreshPeriod, this::refresh);
	}

	public JFrame getWindow() {
		return window;
	}

	public void add(int x, int y, VisualizerWidget widget) {
		JComponent swingComponent = widget.createSwingComponent();
		setPosition(swingComponent, x, y);
		setSizeInCells(swingComponent, widget.getWidthInCells(), widget.getHeightInCells());
		window.add(swingComponent);
		widgets.add(widget);
	}

	public void show() {
		window.pack();
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setVisible(true);
	}

	private void refresh() {
		for (VisualizerWidget widget : widgets) {
			widget.sampleOutputs();
		}
		window.repaint();
	}

	private static void setPosition(Component component, int x, int y) {
		component.setLocation(x * CELL_SIZE, y * CELL_SIZE);
	}

	private static void setSizeInCells(Component component, int widthInCells, int heightInCells) {
		component.setPreferredSize(new Dimension(widthInCells * CELL_SIZE, heightInCells * CELL_SIZE));
		component.setSize(new Dimension(widthInCells * CELL_SIZE, heightInCells * CELL_SIZE));
	}

}
