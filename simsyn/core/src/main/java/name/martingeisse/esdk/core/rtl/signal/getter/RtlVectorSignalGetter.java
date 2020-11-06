package name.martingeisse.esdk.core.rtl.signal.getter;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * This is a potentially faster getter to be used instead of {@link RtlVectorSignal#getValue()}.
 *
 * See {@link RtlBitSignalGetter} for a detailed explanation.
 */
public abstract class RtlVectorSignalGetter {

    public abstract VectorValue getValue();

}
