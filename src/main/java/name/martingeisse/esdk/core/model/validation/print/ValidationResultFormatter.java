package name.martingeisse.esdk.core.model.validation.print;

import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.model.structure.DesignRootItemFinder;
import name.martingeisse.esdk.core.model.structure.DesignStructureAnalyzer;
import name.martingeisse.esdk.core.model.validation.DesignValidationResult;
import name.martingeisse.esdk.core.model.validation.ItemValidationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public final class ValidationResultFormatter {

	private final DesignValidationResult designResult;
	private final ValidationResultPrinter printer;
	private Map<Item, Map<String, Item>> subItemRelation;
	private Set<Item> rootItems;
	private Map<Item, String> references;

	public ValidationResultFormatter(DesignValidationResult designResult, ValidationResultPrinter printer) {
		this.designResult = designResult;
		this.printer = printer;
	}

	public void format() {
		DesignStructureAnalyzer structureAnalyzer = new DesignStructureAnalyzer(designResult.getDesign());
		structureAnalyzer.analyze();
		subItemRelation = structureAnalyzer.getSubItemRelation();
		DesignRootItemFinder rootFinder = new DesignRootItemFinder(subItemRelation);
		rootFinder.findRootItems();
		rootItems = rootFinder.getRootItems();
		references = new HashMap<>();
		for (Item root : rootItems) {
			String reference = "ref" + references.size();
			references.put(root, reference);
		}
		for (Item root : rootItems) {
			print(references.get(root), root);
		}
	}

	private void print(String propertyName, Item item) {
		String itemClass = item.getClass().getSimpleName();
		ItemValidationResult itemResult = designResult.getItemResults().get(item);
		if (hasErrorsOrWarnings(itemResult)) {
			printer.beginItem(propertyName, itemClass);
			printContents(itemResult);
			printer.endItem();
		} else {
			printer.printFoldedSubItem(propertyName, itemClass);
		}

	}

	private boolean hasErrorsOrWarnings(ItemValidationResult itemResult) {
		if (!itemResult.getErrors().isEmpty() || !itemResult.getWarnings().isEmpty()) {
			return true;
		}
		for (Item subItem : subItemRelation.get(itemResult.getItem()).values()) {
			if (rootItems.contains(subItem)) {
				continue;
			}
			if (hasErrorsOrWarnings(designResult.getItemResults().get(subItem))) {
				return true;
			}
		}
		return false;
	}

	private void printContents(ItemValidationResult itemResult) {
		for (String error : itemResult.getErrors()) {
			printer.printError(error);
		}
		for (String warning : itemResult.getWarnings()) {
			printer.printWarning(warning);
		}
		for (Map.Entry<String, Item> itemEntry : subItemRelation.get(itemResult.getItem()).entrySet()) {
			print(itemEntry.getKey(), itemEntry.getValue());
		}
	}

}
