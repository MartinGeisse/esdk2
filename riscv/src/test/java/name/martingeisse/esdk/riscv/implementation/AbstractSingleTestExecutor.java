package name.martingeisse.esdk.riscv.implementation;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.riscv.rtl.Multicycle;
import name.martingeisse.esdk.riscv.rtl.MulticycleTestbench;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public abstract class AbstractSingleTestExecutor {

	private final File textSegmentFile;

	private final Design design;
	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final MulticycleTestbench.Implementation testbench;
	private final Multicycle.Implementation cpu;

	public AbstractSingleTestExecutor(File textSegmentFile) {
		this.textSegmentFile = textSegmentFile;
		design = new Design();
		realm = new RtlRealm(design);
		clock = new RtlClockNetwork(realm);
		testbench = new MulticycleTestbench.Implementation(realm, clock);
		cpu = (Multicycle.Implementation)testbench._cpu;

		new RtlClockedSimulationItem(clock) {

			private VectorValue outputData;
			private boolean stop;

			@Override
			public void computeNextState() {
				outputData = testbench.getOutputEnable().getValue() ? testbench.getOutputData().getValue() : null;
				stop = testbench.getStopSimulation().getValue();
			}

			@Override
			public void updateState() {
				if (outputData != null) {
					handleOutputValue(outputData.getBitsAsInt());
				}
				if (stop) {
					getDesign().stopSimulation();
				}
			}

		};
		new RtlClockGenerator(clock, 10);
	}

	public void execute() throws Exception {
		loadProgram();
		design.simulate();
	}

	private void loadProgram() throws Exception {
		try (FileInputStream in = new FileInputStream(textSegmentFile)) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				testbench._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				testbench._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				testbench._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				testbench._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}
	}

	private int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

	protected abstract void handleOutputValue(int value);

}
