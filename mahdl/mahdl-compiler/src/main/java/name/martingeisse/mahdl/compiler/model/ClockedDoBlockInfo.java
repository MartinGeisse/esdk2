package name.martingeisse.mahdl.compiler.model;

import name.martingeisse.mahdl.common.processor.definition.Register;
import name.martingeisse.mahdl.common.processor.definition.SignalLike;
import name.martingeisse.mahdl.common.processor.expression.InstancePortReference;
import name.martingeisse.mahdl.common.processor.statement.ProcessedDoBlock;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public final class ClockedDoBlockInfo extends DoBlockInfo {

	private final SortedSet<Register> registers;

	public ClockedDoBlockInfo(String name, ProcessedDoBlock doBlock) {
		super(name, doBlock);
		this.registers = new TreeSet<>();
		analyze(doBlock.getBody());
	}

	public SortedSet<Register> getRegisters() {
		return registers;
	}

	@Override
	protected void analyzeAssignmentTo(SignalLike destination) {
		if (destination instanceof Register) {
			registers.add((Register) destination);
		} // else: this is an error and should have been reported already
	}

	@Override
	protected void analyzeAssignmentTo(InstancePortReference destination) {
		// this is an error and should have been reported already
	}

}
