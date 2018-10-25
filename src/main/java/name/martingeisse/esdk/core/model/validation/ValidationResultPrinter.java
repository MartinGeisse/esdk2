package name.martingeisse.esdk.core.model.validation;

/**
 *
 */
public interface ValidationResultPrinter {

	void printFoldedSubItem(String propertyName, String className);
	void beginItem(String propertyName, String className);
	void endItem();
	void printError(String message);
	void printWarning(String message);
	void printReference(String propertyName, String reference);

}
