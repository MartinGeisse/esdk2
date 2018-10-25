package name.martingeisse.esdk.core.model.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 */
public class DesignValidator {

	private final Design design;
	private final Map<Item, ItemValidationResult> itemResults = new HashMap<>();
	private final Set<Item> visitedItems = new HashSet<>();

	public DesignValidator(Design design) {
		this.design = design;
	}

	public void validate() {
		for (Item item : design.getItems()) {
			validate(item);
		}
		// TODO determine toplevel items (loops!)
	}

	private ItemValidationResult validate(Item item) {
		if (!visitedItems.add(item)) {
			return itemResults.get(item);
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
		item.customValidate(context);
		Map<String, ItemValidationResult> subResults = new HashMap<>();
		try {
			validateSubItems(item, item.getClass(), subResults);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// TODO references
		ItemValidationResult result = new ItemValidationResult(item,
			ImmutableList.copyOf(errors), ImmutableList.copyOf(warnings),
			ImmutableMap.copyOf(subResults), ImmutableMap.of()
		);
		itemResults.put(item, result);
		return result;
	}

	private void validateSubItems(Item item, Class<?> currentClass, Map<String, ItemValidationResult> subResultCollector) throws Exception {
		if (currentClass == Item.class) {
			return;
		}
		for (Field field : currentClass.getDeclaredFields()) {
			Object value = field.get(item);
			if (value instanceof Item) {

				String propertyName = field.getName();
				if (subResultCollector.containsKey(propertyName)) {
					propertyName = currentClass.getSimpleName() + '.' + field.getName();
					if (subResultCollector.containsKey(propertyName)) {
						propertyName = currentClass.getName() + '.' + field.getName();
					}
				}

				subResultCollector.put(field.getName(), validate((Item)value));
			}
		}
		validateSubItems(item, currentClass.getSuperclass(), subResultCollector);
	}

}
