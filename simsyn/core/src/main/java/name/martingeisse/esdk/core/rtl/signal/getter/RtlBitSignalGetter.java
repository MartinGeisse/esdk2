package name.martingeisse.esdk.core.rtl.signal.getter;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * This is a potentially faster getter to be used instead of {@link RtlBitSignal#getValue()}.
 *
 * The default implementation is actually slower than getValue() since it just redirects to getValue(), but it
 * provides compatibility. Faster implementations start by being a virtual method from an abstract class instead of
 * an interface method, removing the itable stub overhead.
 *
 * To improve on that, an implementation can inspect input signals that are used to compute this signal, and
 * compile the whole object graph into an improved version. This includes not making interface method calls or
 * even virtual method calls for those input signals; not constructing intermediate {@link VectorValue}s,
 * and so on.
 */
public abstract class RtlBitSignalGetter {

    public abstract boolean getValue();

}
