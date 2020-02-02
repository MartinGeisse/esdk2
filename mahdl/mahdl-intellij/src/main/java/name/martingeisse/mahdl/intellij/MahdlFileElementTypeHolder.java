/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij;

import com.intellij.psi.tree.IFileElementType;

/**
 *
 */
public final class MahdlFileElementTypeHolder {

	public static final IFileElementType FILE_ELEMENT_TYPE = new IFileElementType(MahdlLanguage.INSTANCE);

	// prevent instantiation
	private MahdlFileElementTypeHolder() {
	}

}
