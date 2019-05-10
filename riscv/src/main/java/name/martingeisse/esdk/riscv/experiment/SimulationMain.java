package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
		Computer design = new Computer();
		loadProgram(design.getComputerModule());
		new RtlClockGenerator(design.getClock(), 10);

		JFrame frame = new JFrame("Textmode");
		frame.add(design.getComputerModule().display.getTextmodePanel());
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		new Timer(500, event -> {
			design.getComputerModule().display.getTextmodePanel().repaint();
		}).start();

		design.simulate();
	}

	private static void loadProgram(ComputerModule computerModule) throws Exception {
		try (FileInputStream in = new FileInputStream("riscv/resource/program/build/program.bin")) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				computerModule.memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				computerModule.memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				computerModule.memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				computerModule.memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}
	}

	private static int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

}
