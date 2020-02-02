/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
import name.martingeisse.mahdl.input.cm.CmToken;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class Named implements Comparable<Named> {

	@NotNull
	private final CmToken nameElement;

	public Named(@NotNull CmToken nameElement) {
		this.nameElement = nameElement;
	}

	@NotNull
	public final CmToken getNameElement() {
		return nameElement;
	}

	@NotNull
	public final String getName() {
		return nameElement.getText();
	}

	public abstract void processExpressions(@NotNull ExpressionProcessor expressionProcessor);

	@Override
	public int compareTo(@NotNull Named other) {
		return getName().compareTo(other.getName());
	}

}
