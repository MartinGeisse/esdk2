/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

/**
 *
 */
public interface ToplevelPortConsumer {

	void consumePort(String direction, String name, Integer vectorSize);

}
