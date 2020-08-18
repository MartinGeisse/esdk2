package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.riscv.tools.InstructionDisassembler;

/**
 *
 */
public class CpuStateLogger extends RtlClockedSimulationItem {

	private final Multicycle.Implementation cpu;
	private boolean showRegisters = false;
	private boolean showExecutionStates = false;

	public CpuStateLogger(RtlClockNetwork clockNetwork, Multicycle.Implementation cpu) {
		super(clockNetwork);
		this.cpu = cpu;
	}

	public boolean isShowRegisters() {
		return showRegisters;
	}

	public void setShowRegisters(boolean showRegisters) {
		this.showRegisters = showRegisters;
	}

	public boolean isShowExecutionStates() {
		return showExecutionStates;
	}

	public void setShowExecutionStates(boolean showExecutionStates) {
		this.showExecutionStates = showExecutionStates;
	}

	@Override
	public void computeNextState() {
		if (cpu._state.getValue().equals(Multicycle.Implementation._STATE_DECODE_AND_READ1)) {
			showInstruction();
		} else if (showExecutionStates) {
			showExecutionState();
		}
	}

	@Override
	public void updateState() {
	}

	protected void showInstruction() {
		int instruction = cpu._instructionRegister.getValue().getBitsAsInt();
		System.out.println(hex32(cpu._pc) + ": " + hex32(instruction) + " = " + InstructionDisassembler.disassemble(instruction));
		if (showRegisters) {
			for (int i = 0; i < 32; i += 8) {
				System.out.print("  ");
				for (int j = 0; j < 8; j++) {
					System.out.print("  ");
					int index = i + j;
					if (index < 10) {
						System.out.print(' ');
					}
					System.out.print(index + "=" + hex32(cpu._registers.getMatrix().getRow(index)));
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	protected void showExecutionState() {
		VectorValue state = cpu._state.getValue();
		System.out.print("\tstate " + state);
		if (state.equals(Multicycle.Implementation._STATE_MEM_ACCESS)) {
			System.out.print(" (memory address = " + hex32(cpu._memoryAddressRegister) + ")");
		}
		System.out.println();
	}

	protected static String hex32(RtlVectorSignal signal) {
		return hex32(signal.getValue());
	}

	protected static String hex32(VectorValue value) {
		return hex32(value.getBitsAsInt());
	}

	protected static String hex32(int value) {
		String text = "00000000" + Integer.toHexString(value);
		return text.substring(text.length() - 8);
	}

}
