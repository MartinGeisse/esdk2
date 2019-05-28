package name.martingeisse.esdk.core.rtl.synthesis.prettify;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.RtlAssignment;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlSignalConnector;

/**
 * Performs changes to an {@link RtlRealm} that improve readability of the generated Verilog output without affecting
 * the behavior of the design.
 * <p>
 * Instances of this class are stateful and must not be re-used.
 * <p>
 * Primitive inference should not be affected either (it is considered a bug if inference is affected). Any
 * prettification that is valid on some platform but affects synthesis in any way on another platform must be
 * switchable and disabled by default.
 * <p>
 * Current transformations:
 * - (WIP) assigning names to unnamed items
 * - (planned) flattening nested concatenation (unclear if operands should be chacked whether they are shared with
 * other signals. Performance is not affected though since it is only concatenation, which has no run-time effect)
 */
public class RtlPrettifier {

	private boolean anyNameChanged;

	public void prettify(RtlRealm realm) {
		for (RtlItem item : realm.getItems()) {
			propagateOwnName(item);
		}
		while (true) {
			anyNameChanged = false;
			for (RtlItem item : realm.getItems()) {
				propagateOtherNames(item);
			}
			if (!anyNameChanged) {
				break;
			}
		}
	}

	/**
	 * Propagates the name of the specified item to other items.
	 */
	private void propagateOwnName(RtlItem item) {
		if (item.getName() == null) {
			return;
		}
		System.out.println(item.getName() + " / " + item.getClass());
		if (item instanceof RtlSignalConnector) {
			RtlSignalConnector connector = (RtlSignalConnector) item;
			setName(connector.getConnected(), item.getName());
		} else if (item instanceof RtlBitNotOperation) {
			RtlBitNotOperation not = (RtlBitNotOperation)item;
			setName(not.getOperand(), not.getName() + "_not");
		} else if (item instanceof RtlBitOperation) {
			RtlBitOperation operation = (RtlBitOperation) item;
			String baseName = item.getName() + "_" + operation.getOperator().name().toLowerCase();
			setName(operation.getLeftOperand(), baseName + 'L');
			setName(operation.getRightOperand(), baseName + 'R');
		} else if (item instanceof RtlVectorOperation) {
			RtlVectorOperation operation = (RtlVectorOperation) item;
			String baseName = item.getName() + "_" + operation.getOperator().name().toLowerCase();
			setName(operation.getLeftOperand(), baseName + 'L');
			setName(operation.getRightOperand(), baseName + 'R');
		} else if (item instanceof RtlVectorComparison) {
			RtlVectorComparison comparison = (RtlVectorComparison) item;
			String baseName = item.getName() + "_" + comparison.getOperator().name().toLowerCase();
			setName(comparison.getLeftOperand(), baseName + 'L');
			setName(comparison.getRightOperand(), baseName + 'R');
		} else if (item instanceof RtlConditionalOperation) {
			RtlConditionalOperation conditional = (RtlConditionalOperation)item;
			setName(conditional.getCondition(), item.getName() + "_condition");
			setName(conditional.getOnTrue(), item.getName() + "_then");
			setName(conditional.getOnFalse(), item.getName() + "_else");
		} else if (item instanceof RtlModuleInstance) {
			RtlModuleInstance moduleInstance = (RtlModuleInstance)item;
			for (RtlInstancePort port : moduleInstance.getPorts()) {
				setName(port, moduleInstance.getName() + "_" + port.getPortName());
			}
		} else if (item instanceof RtlSwitchSignal<?>) {
			RtlSwitchSignal<?> swtch = (RtlSwitchSignal<?>)item;
			for (RtlSwitchSignal.Case<?> aCase : swtch.getCases()) {
				setName(aCase.getBranch(), item.getName() + "_" + aCase.getSelectorValues().get(0).getDigits());
			}
			if (swtch.getDefaultSignal() != null) {
				setName(swtch.getDefaultSignal(), item.getName() + "_default");
			}
		} else if (item instanceof RtlOutputPin) {
			RtlOutputPin pin = (RtlOutputPin)item;
			setName(pin.getOutputSignal(), pin.getName());
		} else if (item instanceof RtlBidirectionalPin) {
			RtlBidirectionalPin pin = (RtlBidirectionalPin)item;
			setName(pin.getOutputSignal(), pin.getName() + "_d");
			setName(pin.getOutputEnableSignal(), pin.getName() + "_en");
		} else if (item instanceof RtlIndexSelection) {
			RtlIndexSelection indexSelection = (RtlIndexSelection)item;
			setName(indexSelection.getContainerSignal(), indexSelection.getName() + "_container");
			setName(indexSelection.getIndexSignal(), indexSelection.getName() + "_index");
		} else if (item instanceof RtlConstantIndexSelection) {
			RtlConstantIndexSelection indexSelection = (RtlConstantIndexSelection)item;
			setName(indexSelection.getContainerSignal(), indexSelection.getName() + "_container");
		}
		TODO RtlVectorSignal subclasses
	}

	/**
	 * Propagates the name of other items (for which the specified item knows how to propagate the name).
	 */
	private void propagateOtherNames(RtlItem item) {
		if (item instanceof RtlAssignment) {
			RtlAssignment assignment = (RtlAssignment) item;
			// cannot handle partial assignment for now
			String destinationName = assignment.getDestination().getRtlItem().getName();
			if (destinationName != null) {
				setName(assignment.getSource(), destinationName + "_d");
			}
		} else if (item instanceof RtlWhenStatement) {
			RtlWhenStatement when = (RtlWhenStatement)item;
			setName(when.getCondition().getRtlItem(), "condition");
		}
	}

	private void setName(RtlItemOwned itemOwned, String name) {
		setName(itemOwned.getRtlItem(), name);
	}

	private void setName(RtlItem item, String name) {
		if (item.getName() == null && name != null) {
			anyNameChanged = true;
			item.setName(name);
			propagateOwnName(item);
		}
	}

}
