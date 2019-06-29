package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores names for things in Verilog and performs assignment of generated names.
 */
class Names {

    private final Set<String> names = new HashSet<>();
    private final Set<String> fixedNames = new HashSet<>();
    private final Map<String, MutableInt> prefixNameCounters = new HashMap<>();
    private final Map<RtlProceduralMemory, String> memoryNames = new HashMap<>();

    String assignFixedName(String name) {
        if (!names.add(name)) {
            throw new IllegalStateException("fixed name is already used: " + name);
        }
        names.add(name);
        fixedNames.add(name);
        return name;
    }

    String assignGeneratedName(String prefix) {
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
            if (names.add(name)) {
                return name;
            }

        }
    }

    public String declareProceduralMemory(RtlProceduralMemory memory) {
        String prefix = memory.getName() == null ? "mem" : memory.getName();
        String globalName = assignGeneratedName(prefix);
        memoryNames.put(memory, globalName);
        return globalName;
    }

    String getMemoryName(RtlProceduralMemory memory) {
        return memoryNames.get(memory);
    }

}
