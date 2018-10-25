package name.martingeisse.esdk.core.model.validation;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.model.validation.print.ValidationResultFormatter;
import name.martingeisse.esdk.core.model.validation.print.ValidationResultPrinter;

/**
 *
 */
public final class DesignValidationResult {

	private final Design design;
	private final ImmutableMap<Item, ItemValidationResult> itemResults;

	public DesignValidationResult(Design design, ImmutableMap<Item, ItemValidationResult> itemResults) {
		this.design = design;
		this.itemResults = itemResults;
	}

	public Design getDesign() {
		return design;
	}

	public ImmutableMap<Item, ItemValidationResult> getItemResults() {
		return itemResults;
	}

	public void format(ValidationResultPrinter printer) {
		new ValidationResultFormatter(this, printer).format();
	}

}
