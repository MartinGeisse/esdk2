/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.input.cm;

import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface UnaryOperation extends CmNode {

	@Nullable
	Expression getOperand();

}
