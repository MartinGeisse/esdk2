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
    private final Map<String, VerilogNamed> nameToObject = new HashMap<>();
    private final Map<VerilogNamed, String> objectToName = new HashMap<>();

    public VerilogNames(AbsoluteNames absoluteNames) {
        this.absoluteNames = absoluteNames;
    }

    /**
     * Uses the specified name for an object, ignoring the object's own name.
     */
    void assignFixedName(String name, VerilogNamed object) {
        if (nameToObject.putIfAbsent(name, object) != null) {
            throw new IllegalStateException("fixed name is already used: " + name);
        }
        objectToName.put(object, name);
        fixedNames.add(name);
    }

    /**
     * Generates a name based on the object's own name, possibly adding a number for disambiguation.
     */
    String assignGeneratedName(VerilogNamed object) {
        RtlItem verilogNameSuggestionProvider = object.getVerilogNameSuggestionProvider();
        String prefix;
        if (verilogNameSuggestionProvider == null) {
            prefix = "_object";
        } else {
            prefix = absoluteNames.getAbsoluteName(verilogNameSuggestionProvider);
        }
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
            if (nameToObject.putIfAbsent(name, object) == null) {
                objectToName.put(object, name);
                return name;
            }

        }
    }

    String getName(VerilogNamed object) {
        return objectToName.get(object);
    }

}
