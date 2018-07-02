/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorValue;

/**
 *
 */
public interface RtlVectorAssignmentTarget extends RtlAssignmentTarget {

	int getWidth();

	void setNextValue(RtlVectorValue nextValue);

}
