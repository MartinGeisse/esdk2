package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.util.AbsoluteNames;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores names for things in Verilog and performs assignment of generated names.
 */
class VerilogNames {

	private final AbsoluteNames absoluteNames;
	private final Set<String> fixedNames = new HashSet<>();
	private final Map<String, MutableInt> prefixNameCounters = new HashMap<>();
	private final Map<String, RtlItem> nameToItem = new HashMap<>();
	private final Map<RtlItem, String> itemToName = new HashMap<>();

	public VerilogNames(AbsoluteNames absoluteNames) {
		this.absoluteNames = absoluteNames;
	}

	/**
	 * Uses the specified name for an object, ignoring the object's own name.
	 */
	void assignFixedName(String name, RtlItem item) {
		if (nameToItem.putIfAbsent(name, item) != null) {
			throw new IllegalStateException("fixed name is already used: " + name);
		}
		itemToName.put(item, name);
		fixedNames.add(name);
	}

	/**
	 * Generates a name based on the object's own name, possibly adding a number for disambiguation.
	 */
	String assignGeneratedName(RtlItem item) {
		String prefix = absoluteNames.getAbsoluteName(item);
		MutableInt counter = prefixNameCounters.computeIfAbsent(prefix, p -> new MutableInt());
		while (true) {
			String name = prefix + "__" + counter.intValue();
			counter.increment();

			// If the counter collides with a fixed name, we could just increment again, but we shouldn't: The order
			// in which the fixed and assigned names get reserved is undefined and therefore should not be relevant,
			// so we must do the same as if the generated name came first -- and that throws an exception.
			if (fixedNames.contains(name)) {
				throw new IllegalStateException("assigned name collides with fixed name: " + name);
			}

			// There may still be a collision in the odd case of counter prefixes like "foo" and "foo__1", so to
			// avoid edge cases, we have to check that too, hence the while loop.
			if (nameToItem.putIfAbsent(name, item) == null) {
				itemToName.put(item, name);
				return name;
			}

		}
	}

	String getName(RtlItem item) {
		return itemToName.get(item);
	}

}
