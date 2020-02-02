/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class MahdlSourceFile extends PsiFileBase {

	public MahdlSourceFile(@NotNull FileViewProvider viewProvider) {
		super(viewProvider, MahdlLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType() {
		return MahdlFileType.INSTANCE;
	}

	@Nullable
	public ModuleImpl getModule() {
		for (PsiElement child : getChildren()) {
			if (child instanceof ModuleImpl) {
				return (ModuleImpl) child;
			}
		}
		return null;
	}

}
