package name.martingeisse.esdk.core.rtl.synthesis.prettify;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.signal.RtlBitOperation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorOperation;

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
		if (item instanceof RtlBitOperation) {
			RtlBitOperation operation = (RtlBitOperation) item;
			String baseName = item.getName() + "_" + operation.getOperator().name().toLowerCase();
			setName(operation.getLeftOperand(), baseName + 'L');
			setName(operation.getLeftOperand(), baseName + 'R');
		} else if (item instanceof RtlVectorOperation) {
			RtlVectorOperation operation = (RtlVectorOperation) item;
			String baseName = item.getName() + "_" + operation.getOperator().name().toLowerCase();
			setName(operation.getLeftOperand(), baseName + 'L');
			setName(operation.getLeftOperand(), baseName + 'R');
		}
	}

	/**
	 * Propagates the name of other items (for which the specified item knows how to propagate the name).
	 */
	private void propagateOtherNames(RtlItem item) {

	}

	private void setName(RtlItemOwned itemOwned, String name) {
		setName(itemOwned.getRtlItem(), name);
	}

	private void setName(RtlItem item, String name) {
		if (item.getName() == null) {
			anyNameChanged = true;
			item.setName(name);
			propagateOwnName(item);
		}
	}

}
