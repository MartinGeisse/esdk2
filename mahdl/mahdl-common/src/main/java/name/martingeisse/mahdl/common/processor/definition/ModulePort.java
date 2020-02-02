/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.DataType;
import name.martingeisse.mahdl.input.cm.PortDirection_In;
import name.martingeisse.mahdl.input.cm.PortDirection_Out;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class ModulePort extends SignalLike {

	@NotNull
	private final name.martingeisse.mahdl.input.cm.PortDirection directionElement;

	@NotNull
	private final PortDirection direction;

	public ModulePort(@NotNull CmToken nameElement,
					  @NotNull name.martingeisse.mahdl.input.cm.PortDirection directionElement,
					  @NotNull DataType dataTypeElement,
					  @NotNull ProcessedDataType processedDataType) {
		super(nameElement, dataTypeElement, processedDataType, null);
		this.directionElement = directionElement;
		if (directionElement instanceof PortDirection_In) {
			direction = PortDirection.IN;
		} else if (directionElement instanceof PortDirection_Out) {
			direction = PortDirection.OUT;
		} else {
			throw new IllegalArgumentException("invalid port direction element");
		}
	}

	@NotNull
	public name.martingeisse.mahdl.input.cm.PortDirection getDirectionElement() {
		return directionElement;
	}

	@NotNull
	public PortDirection getDirection() {
		return direction;
	}

}
