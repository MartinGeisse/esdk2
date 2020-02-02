/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.functions;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class BuiltinFunctions {

	public static final ImmutableMap<String, BuiltinFunction> FUNCTIONS;

	static {
		List<BuiltinFunction> functions = new ArrayList<>();
		functions.add(new BitFunction());
		functions.add(new AsciiFunction());
		functions.add(new AsciizFunction());
		functions.add(new LoadMahdlMatrixFileFunction());
		functions.add(new RepeatFunction());

		ImmutableMap.Builder<String, BuiltinFunction> builder = ImmutableMap.builder();
		for (BuiltinFunction function : functions) {
			builder.put(function.getName(), function);
		}
		FUNCTIONS = builder.build();
	}

	// prevent instantiation
	private BuiltinFunctions() {
	}

}
