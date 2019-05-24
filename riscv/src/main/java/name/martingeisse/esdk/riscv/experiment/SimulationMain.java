package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.riscv.experiment.terminal.MyMonitorPanel;

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

		// TerminalPanel terminalPanel = new TerminalPanel(design.getClock());
		// design.getComputerModule().textDisplay.setTerminalPanel(terminalPanel);
		// design.getComputerModule().keyboard.setTerminalPanel(terminalPanel);

		MyMonitorPanel monitorPanel = new MyMonitorPanel(design.getClock(), design.getComputerModule()._textDisplay);

		JFrame frame = new JFrame("Terminal");
		// frame.add(terminalPanel);
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		// new Timer(500, event -> terminalPanel.repaint()).start();
		new Timer(500, event -> monitorPanel.repaint()).start();

		design.simulate();
	}

	static void loadProgram(ComputerModule computerModule) throws Exception {
		try (FileInputStream in = new FileInputStream("riscv/resource/program/build/program.bin")) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				computerModule._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				computerModule._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				computerModule._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				computerModule._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}
	}

	private static int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

}
