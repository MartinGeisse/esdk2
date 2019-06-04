package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.tools.InstructionDisassembler;

/**
 *
 */
public class CpuStateLogger extends RtlClockedSimulationItem {

	private final Multicycle.Implementation cpu;
	private boolean showExecutionStates = false;

	public CpuStateLogger(RtlClockNetwork clockNetwork, Multicycle.Implementation cpu) {
		super(clockNetwork);
		this.cpu = cpu;
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
	}

	protected void showExecutionState() {
		System.out.println("\tstate " + cpu._state.getValue());
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
