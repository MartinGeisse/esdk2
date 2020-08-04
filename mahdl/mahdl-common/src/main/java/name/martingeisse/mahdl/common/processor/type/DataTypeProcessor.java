/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.type;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
import name.martingeisse.mahdl.input.cm.DataType;
import org.jetbrains.annotations.NotNull;

public interface DataTypeProcessor {

	DataTypeProcessor ISOLATED = new DataTypeProcessorImpl(ProcessingSidekick.ISOLATED, ExpressionProcessor.ISOLATED);

	@NotNull
	default ProcessedDataType processDataType(@NotNull DataType dataType) {
		return processDataType(dataType, true);
	}

	@NotNull
	ProcessedDataType processDataType(@NotNull DataType dataType, boolean reportErrors);

}
