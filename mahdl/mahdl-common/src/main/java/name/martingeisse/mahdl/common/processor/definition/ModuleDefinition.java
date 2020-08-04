/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.common.processor.statement.ProcessedDoBlock;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class ModuleDefinition {

	private final boolean isNative;

	@NotNull
	private final ModuleIdentifier identifier;

	@NotNull
	private final ImmutableMap<String, Named> definitions;

	@NotNull
	private final ImmutableList<ProcessedDoBlock> doBlocks;

	public ModuleDefinition(boolean isNative, @NotNull ModuleIdentifier identifier, @NotNull ImmutableMap<String, Named> definitions, @NotNull ImmutableList<ProcessedDoBlock> doBlocks) {
		this.isNative = isNative;
		this.identifier = identifier;
		this.definitions = definitions;
		this.doBlocks = doBlocks;
	}

	public boolean isNative() {
		return isNative;
	}

	@NotNull
	public ModuleIdentifier getIdentifier() {
		return identifier;
	}

	@NotNull
	public ImmutableMap<String, Named> getDefinitions() {
		return definitions;
	}

	@NotNull
	public ImmutableList<ProcessedDoBlock> getDoBlocks() {
		return doBlocks;
	}

}
