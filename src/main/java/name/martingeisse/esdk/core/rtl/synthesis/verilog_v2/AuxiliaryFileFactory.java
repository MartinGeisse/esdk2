/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public interface AuxiliaryFileFactory {

	OutputStream create(String filename) throws IOException;

}
