package name.martingeisse.esdk.core.rtl.util;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralRegister;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.prettify.RtlPrettifier;
import name.martingeisse.esdk.core.util.InfiniteRecursionDetector;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Determines an absolute name for each {@link RtlItem} based on the item's (relative) name, its hierarchy parent,
 * its class, and how it is used.
 * <p>
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
		for (RtlItem item : realm.getItems()) {
			determineAbsoluteName(item);
		}
		usageBasedNameSuggestions = null;
		infiniteRecursionDetector = null;
	}

	private String determineAbsoluteName(RtlItem item) {
		if (infiniteRecursionDetector.begin(item)) {
			String result = determineAbsoluteNameWithoutRecursionDetection(item);
			infiniteRecursionDetector.end(item);
			return result;
		} else {
			return getDefaultName(item);
		}
	}

	private String determineAbsoluteNameWithoutRecursionDetection(RtlItem item) {
		String absoluteName = absoluteNames.get(item);
		if (absoluteName == null) {
			String independentSuggestion = usageBasedNameSuggestions.getIndependentSuggestions().get(item);
			if (independentSuggestion != null) {
				absoluteName = independentSuggestion;
			} else {
				UsageBasedNameSuggestions.PropagatingSuggestion propagatingSuggestion = usageBasedNameSuggestions.getPropagatingSuggestions().get(item);
				if (propagatingSuggestion != null) {
					String originName = determineAbsoluteName(propagatingSuggestion.getOrigin());
					absoluteName = propagatingSuggestion.getNameTransformation().apply(originName);
				} else {
					absoluteName = getDefaultName(item);
				}
			}
			absoluteNames.put(item, absoluteName);
		}
		return absoluteName;
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

	public String getAbsoluteName(RtlItem item) {
		return absoluteNames.get(item);
	}

}
