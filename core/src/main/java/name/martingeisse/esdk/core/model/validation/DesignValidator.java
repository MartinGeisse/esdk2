package name.martingeisse.esdk.core.model.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

import java.util.*;

/**
 *
 */
public class DesignValidator {

	private final Design design;
	private final Set<Item> visitedItems = new HashSet<>();
	private final Map<Item, ItemValidationResult> itemResults = new HashMap<>();

	public DesignValidator(Design design) {
		this.design = design;
	}

	public DesignValidationResult validate() {
		for (Item item : design.getItems()) {
			validate(item);
		}
		return new DesignValidationResult(design, ImmutableMap.copyOf(itemResults));
	}

	private void validate(Item item) {
		if (!visitedItems.add(item)) {
			return;
		}
		List<String> errors = new ArrayList<>();
		List<String> warnings = new ArrayList<>();
		ValidationContext context = new ValidationContext() {

			@Override
			public void reportError(String message) {
				errors.add(message);
			}

			@Override
			public void reportWarning(String message) {
				warnings.add(message);
			}

		};
		item.validate(context);
		itemResults.put(item, new ItemValidationResult(item, ImmutableList.copyOf(errors), ImmutableList.copyOf(warnings)));
	}

}
