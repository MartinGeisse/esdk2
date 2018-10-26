package name.martingeisse.esdk.core.model.structure;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses reflection to obtain an item graph. This can be used for things such as visualizing the design, printing
 * validation errors, and so on.
 *
 * Instances of this class cannot be re-used.
 */
public final class DesignStructureAnalyzer {

	private final Design design;
	private final Map<Item, Map<String, Item>> subItemRelation = new HashMap<>();

	public DesignStructureAnalyzer(Design design) {
		this.design = design;
	}

	public Map<Item, Map<String, Item>> getSubItemRelation() {
		return subItemRelation;
	}

	public void analyze() {
		try {
			for (Item item : design.getItems()) {
				analyze(item);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void analyze(Item item) throws Exception {
		if (subItemRelation.containsKey(item)) {
			return;
		}
		Map<String, Item> subItemCollector = new HashMap<>();
		subItemRelation.put(item, subItemCollector);
		collectSubItems(item, item.getClass(), subItemCollector);
	}


	private void collectSubItems(Item item, Class<?> currentClass, Map<String, Item> subItemCollector) throws Exception {
		if (currentClass == Item.class) {
			return;
		}
		for (Field field : currentClass.getDeclaredFields()) {

			String propertyName = field.getName();
			if (subItemCollector.containsKey(propertyName)) {
				propertyName = currentClass.getSimpleName() + '.' + field.getName();
				if (subItemCollector.containsKey(propertyName)) {
					propertyName = currentClass.getName() + '.' + field.getName();
				}
			}

			field.setAccessible(true);
			Object value = field.get(item);
			if (value instanceof Item) {
				subItemCollector.put(propertyName, (Item) value);
			} else if (value instanceof Iterable<?>) {
				int i = 0;
				for (Object element : (Iterable<?>)value) {
					if (element instanceof Item) {
						subItemCollector.put(propertyName + '[' + i + ']', (Item)element);
					}
					i++;
				}
			}
		}
		collectSubItems(item, currentClass.getSuperclass(), subItemCollector);
	}

}
