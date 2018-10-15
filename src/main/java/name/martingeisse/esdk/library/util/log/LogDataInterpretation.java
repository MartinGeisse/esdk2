package name.martingeisse.esdk.library.util.log;

/**
 * Defines the meaning of type numbers with respect to the interpretation of data bytes.
 * <p>
 * Implementations are stateful and should not be re-used by multiple loggers.
 */
public interface LogDataInterpretation {

	/**
	 * Consumes a byte of data (0..255) according to the specified type (non-negative).
	 */
	void consume(int data, int type);

}
