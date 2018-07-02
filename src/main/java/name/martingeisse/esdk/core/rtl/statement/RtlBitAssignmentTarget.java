/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

/**
 *
 */
public interface RtlBitAssignmentTarget extends RtlAssignmentTarget {

	void setNextValue(boolean nextValue);

}
