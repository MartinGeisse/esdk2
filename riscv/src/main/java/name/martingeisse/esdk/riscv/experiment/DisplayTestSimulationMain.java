package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.riscv.experiment.terminal.MyMonitorPanel;
import name.martingeisse.esdk.riscv.experiment.terminal.TextDisplayController;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class DisplayTestSimulationMain {

	public static void main(String[] args) throws Exception {
		DisplayTestDesign design = new DisplayTestDesign();
		DisplayTest.Implementation displayTest = (DisplayTest.Implementation)design.getDisplayTest();
		loadProgram(displayTest);
		new RtlClockGenerator(design.getClock(), 10);

		MyMonitorPanel monitorPanel = new MyMonitorPanel(design.getClock(), (TextDisplayController.Implementation)displayTest._textDisplay);

		JFrame frame = new JFrame("Terminal");
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> monitorPanel.repaint()).start();

		design.simulate();
	}

	static void loadProgram(DisplayTest.Implementation displayTest) throws Exception {
		try (FileInputStream in = new FileInputStream("riscv/resource/program/build/program.bin")) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				displayTest._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				displayTest._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				displayTest._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				displayTest._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}
	}

	private static int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

}
