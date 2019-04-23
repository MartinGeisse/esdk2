/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public interface RtlVectorAssignmentTarget extends RtlAssignmentTarget {

	int getWidth();

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	VectorValue getNextValue();
	void setNextValue(VectorValue nextValue);

}
