/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Sorting: This class can be sorted by port name. This may mix instance ports from different
 * module instances or even different modules, but we don't care since sorting is only used to
 * keep the generated code from changing randomly.
 *
 * TODO remove, we already have {@link ModuleApi.Port}
 */
public final class InstancePort implements Comparable<InstancePort> {

	@NotNull
	private final String name;

	@NotNull
	private final PortDirection direction;

	@NotNull
	private final ProcessedDataType dataType;

	public InstancePort(@NotNull String name, @NotNull PortDirection direction, @NotNull ProcessedDataType dataType) {
		this.name = name;
		this.direction = direction;
		this.dataType = dataType;
	}

	@NotNull
	public String getName() {
		return name;
	}

	@NotNull
	public PortDirection getDirection() {
		return direction;
	}

	@NotNull
	public ProcessedDataType getDataType() {
		return dataType;
	}

	@Override
	public int compareTo(@NotNull InstancePort other) {
		return name.compareTo(other.getName());
	}

}
