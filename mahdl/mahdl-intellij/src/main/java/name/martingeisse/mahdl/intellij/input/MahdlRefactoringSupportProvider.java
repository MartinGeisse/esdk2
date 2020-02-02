/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij.input;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import name.martingeisse.mahdl.input.cm.impl.ModuleInstanceDefinitionImpl;
import name.martingeisse.mahdl.input.cm.impl.PortDefinitionImpl;
import name.martingeisse.mahdl.input.cm.impl.SignalLikeDefinitionImpl;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MahdlRefactoringSupportProvider extends RefactoringSupportProvider {

	@Override
	public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
		return (element instanceof ModuleImpl) || (element instanceof PortDefinitionImpl) ||
			(element instanceof SignalLikeDefinitionImpl) || (element instanceof ModuleInstanceDefinitionImpl);
	}

}
