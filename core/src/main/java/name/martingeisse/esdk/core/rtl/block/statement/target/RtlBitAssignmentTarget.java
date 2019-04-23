/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

/**
 *
 */
public interface RtlBitAssignmentTarget extends RtlAssignmentTarget {

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	boolean getNextValue();
	void setNextValue(boolean nextValue);

}
