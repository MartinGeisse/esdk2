package name.martingeisse.esdk.core.rtl.signal;

/**
 * Listens to changes in a signal value.
 */
public interface RtlSignalListener {

	void onSignalChanged(RtlSignal changedSignal);

}
