/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import name.martingeisse.mahdl.input.cm.impl.ImplementationItemImpl;
import name.martingeisse.mahdl.input.cm.impl.ImplementationItem_DoBlockImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MahdlFoldingBuilder implements FoldingBuilder {

	@NotNull
	@Override
	public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode astNode, @NotNull Document document) {
		List<FoldingDescriptor> foldingDescriptors = new ArrayList<>();
		collectFoldingRegions(astNode.getPsi(), foldingDescriptors);
		return foldingDescriptors.toArray(new FoldingDescriptor[0]);
	}

	private void collectFoldingRegions(PsiElement psiElement, List<FoldingDescriptor> destination) {
		if (psiElement instanceof ImplementationItem_DoBlockImpl) {
			destination.add(new FoldingDescriptor(psiElement.getNode(), psiElement.getTextRange()));
		} else if (!(psiElement instanceof ImplementationItemImpl)) {
			for (PsiElement child : psiElement.getChildren()) {
				collectFoldingRegions(child, destination);
			}
		}
	}

	@Nullable
	@Override
	public String getPlaceholderText(@NotNull ASTNode astNode) {
		PsiElement psiElement = astNode.getPsi();
		if (psiElement instanceof ImplementationItem_DoBlockImpl) {
			ImplementationItem_DoBlockImpl doBlock = (ImplementationItem_DoBlockImpl) psiElement;
			return "do (" + doBlock.getTriggerPsi().getText() + ") {...}";
		}
		return psiElement.toString();
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
		return false;
	}

}
