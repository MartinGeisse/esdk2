/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.util;

/**
 *
 */
public class StringUtil {

	public static String toHexString32(int value) {
		String s = ("00000000" + Integer.toHexString(value));
		return s.substring(s.length() - 8, s.length());
	}
}
