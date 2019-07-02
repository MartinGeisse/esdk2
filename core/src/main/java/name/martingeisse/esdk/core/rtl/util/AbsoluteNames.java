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
import name.martingeisse.esdk.core.util.InfiniteRecursionDetector;
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

	private UsageBasedNameSuggestions usageBasedNameSuggestions;
	private InfiniteRecursionDetector<RtlItem> infiniteRecursionDetector;
	private final Map<RtlItem, String> absoluteNames;

	public AbsoluteNames(RtlRealm realm) {
		usageBasedNameSuggestions = new UsageBasedNameSuggestions(realm);
		infiniteRecursionDetector = new InfiniteRecursionDetector<>();
		absoluteNames = new HashMap<>();

		// TODO

		usageBasedNameSuggestions = null;
		infiniteRecursionDetector = null;
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


}
