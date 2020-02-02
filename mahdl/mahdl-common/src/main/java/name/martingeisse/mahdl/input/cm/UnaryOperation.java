/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.input.cm;

import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Expression;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface UnaryOperation extends CmNode {

	@Nullable
	Expression getOperand();

}
