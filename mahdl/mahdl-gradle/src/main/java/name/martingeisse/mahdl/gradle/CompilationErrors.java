/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.gradle;

import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.impl.CmNodeImpl;
import name.martingeisse.mahdl.input.cm.impl.ModuleWrapper;

/**
 *
 */
public class CompilationErrors {

	private static final boolean ENABLE_DIAGNOSTICS = false;

	private static final Object dummy = new Object();
	private static final ThreadLocal<Object> hasErrors = new ThreadLocal<>();

	public static boolean isHasErrors() {
		return hasErrors.get() != null;
	}

	private static void printDiagnosticPrefix() {
		System.err.println("DIAGNOSTIC:");
	}

	public static void reportDiagnostic(CmLinked errorSource, String message) {
		if (ENABLE_DIAGNOSTICS) {
			printDiagnosticPrefix();
			reportError(errorSource, message);
		}
	}

	public static void reportError(CmLinked errorSource, String message) {
		reportError(errorSource, message, null);
	}

	public static void reportDiagnostic(CmLinked errorSource, String message, Throwable exception) {
		if (ENABLE_DIAGNOSTICS) {
			printDiagnosticPrefix();
			reportError(errorSource, message, exception);
		}
	}

	public static void reportError(CmLinked errorSource, String message, Throwable exception) {
		String path = "unknown file";
		int row = 0, column = 0;
		if (errorSource != null) {
			CmNodeImpl nodeImpl = (CmNodeImpl) errorSource.getCmNode();
			row = nodeImpl.getRow();
			column = nodeImpl.getColumn();
			ModuleWrapper moduleWrapper = ModuleWrapper.get(nodeImpl);
			if (moduleWrapper != null && moduleWrapper.getFile() != null) {
				path = moduleWrapper.getFile().getPath();
			}
		}
		CompilationErrors.reportError(path, row, column, message, exception);
	}

	public static void reportDiagnostic(String path, int row, int column, String message) {
		if (ENABLE_DIAGNOSTICS) {
			printDiagnosticPrefix();
			reportError(path, row, column, message);
		}
	}

	public static void reportError(String path, int row, int column, String message) {
		reportError(path, row, column, message, null);
	}

	public static void reportDiagnostic(String path, int row, int column, String message, Throwable exception) {
		if (ENABLE_DIAGNOSTICS) {
			printDiagnosticPrefix();
			reportError(path, row, column, message, exception);
		}
	}

	public static void reportError(String path, int row, int column, String message, Throwable exception) {
		hasErrors.set(dummy);
		System.err.println(path + ':' + row + ": error: " + message);
		if (exception != null) {
			exception.printStackTrace(System.err);
		}
	}

	public static void failOnErrors() {
		if (isHasErrors()) {
			throw new CompilationFailedException("Compilation failed; see the compiler error output for details.");
		}
	}

	public static void clearErrors() {
		hasErrors.remove();
	}

}
