package name.martingeisse.esdk.model;

/**
 * Used to identify objects during transformation to a simulation model or synthesis model.
 *
 * A high-level model node of type 'A' that can be referred to by other nodes (i.e. almost all of them) provides
 * one or more Key(A, B) objects that are used to identify the objects it produces during transformation to a
 * simulation model. The full identifier of such an output object consists of the high-level model node itself
 * plus a key object. (This means that key objects can be provided as singletons and are not bound to specific
 * high-level model nodes, simplifying things). The 'B' type of the key is the type of the output object, allowing
 * type-safe access to them.
 *
 * Similarly, a different set of keys is used to identify the objects of the synthesis (RTL) model. The set of
 * objects may be different than for simulation, and the set of 'B' types is certainly different, hence a
 * distinct set of keys makes sense.
 *
 * During transformation, model nodes should first register the output objects that represent the result of the
 * transformation, then ask for other transformed objects and fill the fields of that output object. This avoids
 * infinite recursion when producing a cyclic output graph.
 */
public final class Key<A, B> {
}
