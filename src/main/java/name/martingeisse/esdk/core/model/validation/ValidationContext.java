package name.martingeisse.esdk.core.model.validation;

/**
 *
 */
public interface ValidationContext {

	void reportError(String message);

	void reportWarning(String message);

}
