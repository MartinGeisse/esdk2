/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

/**
 *
 */
public interface PinConsumer {

	void consumePin(String direction, String name);

}
