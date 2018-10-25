package name.martingeisse.esdk.core.model.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.model.Item;

import java.util.Map;

/**
 *
 */
public final class ItemValidationResult {

	private final Item item;
	private final ImmutableList<String> errors;
	private final ImmutableList<String> warnings;
	private final ImmutableMap<String, ItemValidationResult> subItemResults;
	private final ImmutableMap<String, String> subItemReferences;

	public ItemValidationResult(Item item, ImmutableList<String> errors, ImmutableList<String> warnings, ImmutableMap<String, ItemValidationResult> subItemResults, ImmutableMap<String, String> subItemReferences) {
		if (item == null) {
			throw new IllegalArgumentException("item is null");
		}
		if (errors == null) {
			throw new IllegalArgumentException("errors is null");
		}
		if (warnings == null) {
			throw new IllegalArgumentException("warnings is null");
		}
		if (subItemResults == null) {
			throw new IllegalArgumentException("subItemResults is null");
		}
		if (subItemReferences == null) {
			throw new IllegalArgumentException("subItemReferences is null");
		}
		this.item = item;
		this.errors = errors;
		this.warnings = warnings;
		this.subItemResults = subItemResults;
		this.subItemReferences = subItemReferences;
	}

	public Item getItem() {
		return item;
	}

	public ImmutableList<String> getErrors() {
		return errors;
	}

	public ImmutableList<String> getWarnings() {
		return warnings;
	}

	public ImmutableMap<String, ItemValidationResult> getSubItemResults() {
		return subItemResults;
	}

	public boolean hasErrorsOrWarnings() {
		if (!errors.isEmpty() || !warnings.isEmpty()) {
			return true;
		}
		for (ItemValidationResult subResult : subItemResults.values()) {
			if (subResult.hasErrorsOrWarnings()) {
				return true;
			}
		}
		return false;
	}

	public void print(ValidationResultPrinter printer, String propertyName) {
		String itemClass = item.getClass().getSimpleName();
		if (hasErrorsOrWarnings()) {
			printer.beginItem(propertyName, itemClass);
			printContents(printer);
			printer.endItem();
		} else {
			printer.printFoldedSubItem(propertyName, itemClass);
		}

	}

	public void printContents(ValidationResultPrinter printer) {
		for (String error : errors) {
			printer.printError(error);
		}
		for (String warning : warnings) {
			printer.printWarning(warning);
		}
		for (Map.Entry<String, ItemValidationResult> entry : subItemResults.entrySet()) {
			entry.getValue().print(printer, entry.getKey());
		}
		for (Map.Entry<String, String> entry : subItemReferences.entrySet()) {
			printer.printReference(entry.getKey(), entry.getValue());
		}
	}

}
