package name.martingeisse.esdk.core.model.validation;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.model.Item;

/**
 *
 */
public final class ItemValidationResult {

	private final Item item;
	private final ImmutableList<String> errors;
	private final ImmutableList<String> warnings;

	public ItemValidationResult(Item item, ImmutableList<String> errors, ImmutableList<String> warnings) {
		if (item == null) {
			throw new IllegalArgumentException("item is null");
		}
		if (errors == null) {
			throw new IllegalArgumentException("errors is null");
		}
		if (warnings == null) {
			throw new IllegalArgumentException("warnings is null");
		}
		this.item = item;
		this.errors = errors;
		this.warnings = warnings;
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

}
