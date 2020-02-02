/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.gradle;

/**
 *
 */
public class CompilationFailedException extends RuntimeException {

	public CompilationFailedException() {
	}

	public CompilationFailedException(String message) {
		super(message);
	}

	public CompilationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilationFailedException(Throwable cause) {
		super(cause);
	}

}
