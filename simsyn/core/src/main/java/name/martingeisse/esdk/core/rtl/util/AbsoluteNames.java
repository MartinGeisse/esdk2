package name.martingeisse.esdk.core.rtl.util;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralRegister;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.util.InfiniteRecursionDetector;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Determines an absolute name for each {@link RtlItem} based on the item's (relative) name, its hierarchy parent,
 * its class, and how it is used.
 * <p>
 * TODO take hierarchy parent into account
 */
public class AbsoluteNames {

	private final RtlRealm realm;
	private UsageBasedNameSuggestions usageBasedNameSuggestions;
	private InfiniteRecursionDetector<RtlItem> infiniteRecursionDetector;
	private final Map<RtlItem, String> absoluteNames;

	public AbsoluteNames(RtlRealm realm) {
		this.realm = realm;
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
			if (item.getName() != null) {
				absoluteName = item.getName();
			} else {
				UsageBasedNameSuggestions.PropagatingSuggestion propagatingSuggestion = usageBasedNameSuggestions.getPropagatingSuggestions().get(item);
				if (propagatingSuggestion != null) {
					String originName = determineAbsoluteName(propagatingSuggestion.getOrigin());
					absoluteName = propagatingSuggestion.getNameTransformation().apply(originName);
				} else {
					String independentSuggestion = usageBasedNameSuggestions.getIndependentSuggestions().get(item);
					if (independentSuggestion != null) {
						absoluteName = independentSuggestion;
					} else {
						absoluteName = getDefaultName(item);
					}
				}
			}
			if (absoluteName == null) {
				// This should not happen, i.e. none of the above cases should produce null, but it case it happens
				// anyway we want to catch the error early.
				throw new RuntimeException("could not determine absolute name for item: " + item);
			}
			absoluteNames.put(item, absoluteName);
		}
		return absoluteName;
	}

	protected String getDefaultName(RtlItem item) {
		if (item instanceof RtlProceduralRegister) {
			return "register";
		} else if (item instanceof RtlProceduralMemory || item instanceof RtlMemory) {
			return "memory";
		} else if (item instanceof RtlSignal) {
			return "signal";
		} else if (item instanceof RtlModuleInstance) {
			String moduleName = ((RtlModuleInstance) item).getModuleName();
			return moduleName == null ? "instance" : StringUtils.uncapitalize(moduleName);
		}
		String className = item.getClass().getSimpleName();
		if (className.startsWith("Rtl")) {
			className = className.substring(3);
		}
		return StringUtils.uncapitalize(className);
	}

	public String getAbsoluteName(RtlItem item) {
		if (item.getRealm() != realm) {
			throw new IllegalArgumentException("wrong realm for item: " + item);
		}
		String absoluteName = absoluteNames.get(item);
		if (absoluteName == null) {
			throw new IllegalArgumentException("no name for item: " + item);
		}
		return absoluteName;
	}

}
