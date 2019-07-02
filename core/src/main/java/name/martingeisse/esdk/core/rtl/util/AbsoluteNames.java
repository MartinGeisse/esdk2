package name.martingeisse.esdk.core.rtl.util;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemoryConstantIndexSelection;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemoryIndexSelection;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralRegister;
import name.martingeisse.esdk.core.rtl.block.statement.RtlAssignment;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.memory.*;
import name.martingeisse.esdk.core.rtl.module.RtlInstanceInputPort;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.prettify.RtlPrettifier;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Determines an absolute name for each {@link RtlItem} based on the item's (relative) name, its hierarchy parent,
 * its class, and how it is used.
 * <p>
 * TODO this class currently sets names but should actually store them and leave the item's names alone
 * TODO take hierarchy parent into account
 * TODO replace {@link RtlPrettifier} by this class
 */
public class AbsoluteNames {

	private final Map<RtlItem, UsageBasedNameSuggestion> usageBasedNameSuggestions = new HashMap<>();
	private final Map<RtlItem, String> absoluteNames = new HashMap<>();

	public AbsoluteNames(RtlRealm realm) {

		// determine usage-based name suggestions
		for (RtlItem item : realm.getItems()) {
			if (item instanceof RtlSignalConnector) {
				RtlSignalConnector connector = (RtlSignalConnector) item;
				suggest(connector.getConnected().getRtlItem(), connector, name -> name);
			} else if (item instanceof RtlBitNotOperation) {
				RtlBitNotOperation not = (RtlBitNotOperation) item;
				suggest(not.getOperand(), not, name -> name + "_not");
			} else if (item instanceof RtlBitOperation) {
				RtlBitOperation operation = (RtlBitOperation) item;
				suggest(operation.getLeftOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'L');
				suggest(operation.getRightOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'R');
			} else if (item instanceof RtlVectorOperation) {
				RtlVectorOperation operation = (RtlVectorOperation) item;
				suggest(operation.getLeftOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'L');
				suggest(operation.getRightOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'R');
			} else if (item instanceof RtlVectorComparison) {
				RtlVectorComparison comparison = (RtlVectorComparison) item;
				suggest(comparison.getLeftOperand(), comparison, name -> name + "_" + comparison.getOperator().name().toLowerCase() + 'L');
				suggest(comparison.getRightOperand(), comparison, name -> name + "_" + comparison.getOperator().name().toLowerCase() + 'R');
			} else if (item instanceof RtlConditionalOperation) {
				RtlConditionalOperation conditional = (RtlConditionalOperation) item;
				suggest(conditional.getCondition(), conditional, name -> name + "_condition");
				suggest(conditional.getOnTrue(), conditional, name -> name + "_then");
				suggest(conditional.getOnFalse(), conditional, name -> name + "_else");
			} else if (item instanceof RtlModuleInstance) {
				RtlModuleInstance moduleInstance = (RtlModuleInstance) item;
				for (RtlInstancePort port : moduleInstance.getPorts()) {
					suggest(port, moduleInstance, name -> name + '_' + port.getPortName());
				}
			} else if (item instanceof RtlInstanceInputPort) {
				RtlInstanceInputPort port = (RtlInstanceInputPort) item;
				suggest(port.getAssignedSignal(), port, name -> name);
			} else if (item instanceof RtlSwitchSignal<?>) {
				RtlSwitchSignal<?> swtch = (RtlSwitchSignal<?>) item;
				for (RtlSwitchSignal.Case<?> aCase : swtch.getCases()) {
					suggest(aCase.getBranch(), swtch, name -> name + "_" + aCase.getSelectorValues().get(0).getDigits());
				}
				if (swtch.getDefaultSignal() != null) {
					suggest(swtch.getDefaultSignal(), swtch, name -> name + "_default");
				}
			} else if (item instanceof RtlOutputPin) {
				RtlOutputPin pin = (RtlOutputPin) item;
				suggest(pin.getOutputSignal(), pin, name -> name);
			} else if (item instanceof RtlBidirectionalPin) {
				RtlBidirectionalPin pin = (RtlBidirectionalPin) item;
				suggest(pin.getOutputSignal(), pin, name -> name + "_d");
				suggest(pin.getOutputEnableSignal(), pin, name -> name + "_en");
			} else if (item instanceof RtlIndexSelection) {
				RtlIndexSelection indexSelection = (RtlIndexSelection) item;
				suggest(indexSelection.getContainerSignal(), indexSelection, name -> name + "_container");
				suggest(indexSelection.getIndexSignal(), indexSelection, name -> name + "_index");
			} else if (item instanceof RtlConstantIndexSelection) {
				RtlConstantIndexSelection indexSelection = (RtlConstantIndexSelection) item;
				suggest(indexSelection.getContainerSignal(), indexSelection, name -> name + "_container");
			} else if (item instanceof RtlConcatenation) {
				RtlConcatenation concatenation = (RtlConcatenation) item;
				int i = 0;
				for (RtlSignal signal : concatenation.getSignals()) {
					int finalI = i;
					suggest(signal, concatenation, name -> name + "_element" + finalI);
					i++;
				}
			} else if (item instanceof RtlBitRepetition) {
				RtlBitRepetition repetition = (RtlBitRepetition) item;
				suggest(repetition.getBitSignal(), repetition, name -> name + "_element");
			} else if (item instanceof RtlVectorRepetition) {
				RtlVectorRepetition repetition = (RtlVectorRepetition) item;
				suggest(repetition.getVectorSignal(), repetition, name -> name + "_element");
			} else if (item instanceof RtlLookupTable) {
				RtlLookupTable lookupTable = (RtlLookupTable) item;
				suggest(lookupTable.getMemory(), lookupTable, name -> name);
			} else if (item instanceof RtlMemory) {
				RtlMemory memory = (RtlMemory) item;
				int i = 0;
				for (RtlMemoryPort port : memory.getPorts()) {
					int finalI = i;
					suggest(port, memory, name -> name + "_port" + finalI);
					i++;
				}
			} else if (item instanceof RtlAsynchronousMemoryReadPort) {
				RtlAsynchronousMemoryReadPort port = (RtlAsynchronousMemoryReadPort) item;
				suggest(port.getAddressSignal(), port, name -> name + "_address");
				suggest(port.getReadDataSignal(), port, name -> name + "_data");
			} else if (item instanceof RtlSynchronousMemoryPort) {
				RtlSynchronousMemoryPort port = (RtlSynchronousMemoryPort) item;
				suggest(port.getClockEnableSignal(), port, name -> name + "_enable");
				suggest(port.getAddressSignal(), port, name -> name + "_address");
				suggest(port.getReadDataSignal(), port, name -> name + "_readData");
				suggest(port.getWriteDataSignal(), port, name -> name + "_writeData");
				suggest(port.getWriteEnableSignal(), port, name -> name + "_writeEnable");
			} else if (item instanceof RtlVectorNotOperation) {
				RtlVectorNotOperation operation = (RtlVectorNotOperation) item;
				suggest(operation.getOperand(), operation, name -> name + "_not");
			} else if (item instanceof RtlVectorNegateOperation) {
				RtlVectorNegateOperation operation = (RtlVectorNegateOperation) item;
				suggest(operation.getOperand(), operation, name -> name + "_neg");
			} else if (item instanceof RtlProceduralMemoryIndexSelection) {
				RtlProceduralMemoryIndexSelection indexSelection = (RtlProceduralMemoryIndexSelection) item;
				suggest(indexSelection.getMemory(), indexSelection, name -> name + "_container");
				suggest(indexSelection.getIndexSignal(), indexSelection, name -> name + "_index");
			} else if (item instanceof RtlProceduralMemoryConstantIndexSelection) {
				RtlProceduralMemoryConstantIndexSelection indexSelection = (RtlProceduralMemoryConstantIndexSelection) item;
				suggest(indexSelection.getMemory(), indexSelection, name -> name + "_container");
			} else if (item instanceof RtlOneBitVectorSignal) {
				RtlOneBitVectorSignal signal = (RtlOneBitVectorSignal) item;
				suggest(signal.getBitSignal(), signal, name -> name);
			} else if (item instanceof RtlShiftOperation) {
				RtlShiftOperation operation = (RtlShiftOperation) item;
				suggest(operation.getLeftOperand(), item, name -> name + "_shiftL");
				suggest(operation.getRightOperand(), item, name -> name + "_shiftR");
			} else if (item instanceof RtlRangeSelection) {
				RtlRangeSelection rangeSelection = (RtlRangeSelection) item;
				suggest(rangeSelection.getContainerSignal(), rangeSelection, name -> name + "_container");
			} else if (item instanceof RtlAssignment) {
				RtlAssignment assignment = (RtlAssignment) item;
				// cannot handle partial assignment for now
				RtlItem destination = assignment.getDestination().getRtlItem();
				suggest(assignment.getSource(), destination, name -> name + "_d");
			} else if (item instanceof RtlWhenStatement) {
				// TODO this is wrong. The name "condition" can be used even if the when-statement is unnamed!
				RtlWhenStatement when = (RtlWhenStatement) item;
				suggest(when.getCondition().getRtlItem(), when, name -> "condition");
			}

		}
	}

	private void suggest(RtlItemOwned target, RtlItemOwned origin, Function<String, String> nameTransformation) {
		usageBasedNameSuggestions.put(target.getRtlItem(), new UsageBasedNameSuggestion(origin.getRtlItem(), nameTransformation));
	}

	public String getAbsoluteName(RtlItem item) {
		return absoluteNames.get(item);
	}

	/*
	protected String getDefaultName(RtlItem item) {
		if (item instanceof RtlProceduralRegister) {
			return "register";
		} else if (item instanceof RtlProceduralMemory) {
			return "memory";
		} else if (item instanceof RtlSignal) {
			return "signal";
		}
		String className = item.getClass().getSimpleName();
		if (className.startsWith("Rtl")) {
			className = className.substring(3);
		}
		return StringUtils.uncapitalize(className);
	}
	*/

	private final class UsageBasedNameSuggestion {

		RtlItem origin;
		Function<String, String> nameTransformation;

		public UsageBasedNameSuggestion(RtlItem origin, Function<String, String> nameTransformation) {
			this.origin = origin;
			this.nameTransformation = nameTransformation;
		}

	}

}
