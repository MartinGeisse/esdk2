/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Design {

	private final List<Item> items = new ArrayList<>();

	void register(Item item) {
		items.add(item);
	}

}
