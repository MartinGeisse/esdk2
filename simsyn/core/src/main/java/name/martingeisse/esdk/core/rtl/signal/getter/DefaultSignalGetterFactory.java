package name.martingeisse.esdk.core.rtl.signal.getter;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * This factory must not be called until the whole simulation model has been constructed in its final form since the
 * model structure may be encoded into the returned getter.
 */
public class DefaultSignalGetterFactory {

    public static RtlBitSignalGetter getGetter(RtlBitSignal signal) {
        if (signal instanceof RtlBitSignalConnector) {
            return getGetter(((RtlBitSignalConnector) signal).getConnected());
        }
        return new RtlBitSignalGetter() {
            @Override
            public boolean getValue() {
                return signal.getValue();
            }
        };
    }

    public static RtlVectorSignalGetter getGetter(RtlVectorSignal signal) {
        if (signal instanceof RtlVectorSignalConnector) {
            return getGetter(((RtlVectorSignalConnector) signal).getConnected());
        }
        return new RtlVectorSignalGetter() {
            @Override
            public VectorValue getValue() {
                return signal.getValue();
            }
        };
    }

}
