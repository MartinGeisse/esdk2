/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij.input;

import com.intellij.psi.tree.IElementType;
import name.martingeisse.mahdl.intellij.MahdlLanguage;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MahdlElementType extends IElementType {

	public MahdlElementType(@NotNull String debugName) {
		super(debugName, MahdlLanguage.INSTANCE);
	}

}
