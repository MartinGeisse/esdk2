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
import name.martingeisse.esdk.library.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Determines an absolute name for each {@link RtlItem} based on the item's (relative) name, its hierarchy parent,
 * its class, and how it is used.
 *
 * TODO this class currently sets names but should actually store them and leave the item's names alone
 * TODO take hierarchy parent into account
 */
public class AbsoluteNames {

    private final Map<RtlItem, String> absoluteNames = new HashMap<>();
    private boolean anyNameChanged;

    public AbsoluteNames(RtlRealm realm) {
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
        for (RtlItem item : realm.getItems()) {
            if (item.getName() == null) {
                item.setName(getDefaultName(item));
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
            RtlModuleInstance moduleInstance = (RtlModuleInstance) item;
            for (RtlInstancePort port : moduleInstance.getPorts()) {
                setName(port, moduleInstance.getName() + "_" + port.getPortName());
            }
        } else if (item instanceof RtlInstanceInputPort) {
            RtlInstanceInputPort port = (RtlInstanceInputPort)item;
            setName(port.getAssignedSignal(), port.getName());
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
        } else if (item instanceof RtlConcatenation) {
            RtlConcatenation concatenation = (RtlConcatenation)item;
            int i = 0;
            for (RtlSignal signal : concatenation.getSignals()) {
                setName(signal, concatenation.getName() + "_element" + i);
                i++;
            }
        } else if (item instanceof RtlBitRepetition) {
            RtlBitRepetition repetition = (RtlBitRepetition)item;
            setName(repetition.getBitSignal(), repetition.getName() + "_element");
        } else if (item instanceof RtlVectorRepetition) {
            RtlVectorRepetition repetition = (RtlVectorRepetition)item;
            setName(repetition.getVectorSignal(), repetition.getName() + "_element");
        } else if (item instanceof RtlLookupTable) {
            RtlLookupTable lookupTable = (RtlLookupTable)item;
            setName(lookupTable.getMemory(), lookupTable.getName());
        } else if (item instanceof RtlMemory) {
            RtlMemory memory = (RtlMemory)item;
            int i = 0;
            for (RtlMemoryPort port : memory.getPorts()) {
                setName(port, memory.getName() + "_port" + i);
                i++;
            }
        } else if (item instanceof RtlAsynchronousMemoryReadPort) {
            RtlAsynchronousMemoryReadPort port = (RtlAsynchronousMemoryReadPort)item;
            setName(port.getAddressSignal(), port.getName() + "_address");
            setName(port.getReadDataSignal(), port.getName() + "_data");
        } else if (item instanceof RtlSynchronousMemoryPort) {
            RtlSynchronousMemoryPort port = (RtlSynchronousMemoryPort)item;
            setName(port.getClockEnableSignal(), port.getName() + "_enable");
            setName(port.getAddressSignal(), port.getName() + "_address");
            setName(port.getReadDataSignal(), port.getName() + "_readData");
            setName(port.getWriteDataSignal(), port.getName() + "_writeData");
            setName(port.getWriteEnableSignal(), port.getName() + "_writeEnable");
        } else if (item instanceof RtlVectorNotOperation) {
            RtlVectorNotOperation operation = (RtlVectorNotOperation)item;
            setName(operation.getOperand(), operation.getName() + "_not");
        } else if (item instanceof RtlVectorNegateOperation) {
            RtlVectorNegateOperation operation = (RtlVectorNegateOperation)item;
            setName(operation.getOperand(), operation.getName() + "_neg");
        } else if (item instanceof RtlProceduralMemoryIndexSelection) {
            RtlProceduralMemoryIndexSelection indexSelection = (RtlProceduralMemoryIndexSelection)item;
            setName(indexSelection.getMemory(), indexSelection.getName() + "_container");
            setName(indexSelection.getIndexSignal(), indexSelection.getName() + "_index");
        } else if (item instanceof RtlProceduralMemoryConstantIndexSelection) {
            RtlProceduralMemoryConstantIndexSelection indexSelection = (RtlProceduralMemoryConstantIndexSelection)item;
            setName(indexSelection.getMemory(), indexSelection.getName() + "_container");
        } else if (item instanceof RtlOneBitVectorSignal) {
            RtlOneBitVectorSignal signal = (RtlOneBitVectorSignal)item;
            setName(signal.getBitSignal(), signal.getName());
        } else if (item instanceof RtlShiftOperation) {
            RtlShiftOperation operation = (RtlShiftOperation)item;
            String baseName = item.getName() + "_shift";
            setName(operation.getLeftOperand(), baseName + 'L');
            setName(operation.getRightOperand(), baseName + 'R');
        } else if (item instanceof RtlRangeSelection) {
            RtlRangeSelection rangeSelection = (RtlRangeSelection)item;
            setName(rangeSelection.getContainerSignal(), rangeSelection.getName() + "_container");
        }
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

    public String getName(RtlItem item) {
        return absoluteNames.get(item);
    }

}