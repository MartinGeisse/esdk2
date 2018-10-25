package name.martingeisse.esdk.core.model.structure;

import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatement;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Based on the "sub item" relation from the {@link DesignStructureAnalyzer}, this class detects root nodes for a
 * design.
 */
public final class DesignRootItemFinder {

	private final Map<Item, Map<String, Item>> subItemRelation;
	private final Set<Item> unreachedItems;
	private final Set<Item> rootItems;

	public DesignRootItemFinder(Map<Item, Map<String, Item>> subItemRelation) {
		this.subItemRelation = subItemRelation;
		this.unreachedItems = new HashSet<>(subItemRelation.keySet());
		this.rootItems = new HashSet<>();
	}

	public Set<Item> getRootItems() {
		return rootItems;
	}

	public void findRootItems() {

		// unreachable items must be roots
		while (true) {
			Item item = findUnreachableItem();
			if (item == null) {
				break;
			}
			makeRoot(item);
		}

		// find best unreached items to break cycles
		while (true) {
			Item item = findBestUnreachedItem();
			if (item == null) {
				break;
			}
			makeRoot(item);
		}

		// fallback: all items should be reached now, but in case one isn't, we make it a root to avoid errors
		while (!unreachedItems.isEmpty()) {
			makeRoot(unreachedItems.iterator().next());
		}

	}

	private void makeRoot(Item item) {
		rootItems.add(item);
		reach(item);
	}

	private void reach(Item item) {
		if (unreachedItems.remove(item)) {
			for (Item subItem : subItemRelation.get(item).values()) {
				reach(subItem);
			}
		}
	}

	private Item findUnreachableItem() {
		Set<Item> stillUnreachableItems = new HashSet<>(unreachedItems);
		for (Map<String, Item> map : subItemRelation.values()) {
			stillUnreachableItems.removeAll(map.values());
		}
		if (stillUnreachableItems.isEmpty()) {
			return null;
		} else {
			return stillUnreachableItems.iterator().next();
		}
	}

	private Item findBestUnreachedItem() {
		Item bestItem = null;
		int bestScore = -1;
		for (Item item : unreachedItems) {
			int score = calculateScore(item);
			if (score > bestScore) {
				bestItem = item;
				bestScore = score;
			}
		}
		return bestItem;
	}

	private int calculateScore(Item item) {

		// signals and statements are useless if not used anywhere, so they make very bad roots
		if (item instanceof RtlSignal || item instanceof RtlStatement) {
			return 0;
		}

		// RTL realms make very good roots
		if (item instanceof RtlRealm) {
			return 1000;
		}

		// standard score
		return 1;

	}

}
