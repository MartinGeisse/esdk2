/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import name.martingeisse.mahdl.input.Symbols;
import name.martingeisse.mahdl.input.cm.CmUtil;
import name.martingeisse.mahdl.input.cm.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MahdlStructureViewFactory implements PsiStructureViewFactory {

	private static final TokenSet VISIBLE_ELEMENTS = TokenSet.create(
		Symbols.module,
		Symbols.synthetic_List_PortDefinitionGroup,
		Symbols.implementationItem_SignalLikeDefinitionGroup,
		Symbols.implementationItem_DoBlock,
		Symbols.implementationItem_ModuleInstanceDefinitionGroup
	);

	private static boolean shouldInclude(PsiElement element) {
		return VISIBLE_ELEMENTS.contains(element.getNode().getElementType());
	}

	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(@NotNull PsiFile psiFile) {
		if (!(psiFile instanceof MahdlSourceFile)) {
			return null;
		}
		ModuleImpl module = ((MahdlSourceFile) psiFile).getModule();
		if (module == null) {
			return null;
		}
		return new TreeBasedStructureViewBuilder() {
			@NotNull
			@Override
			public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
				return new TextEditorBasedStructureViewModel(editor, psiFile) {

					@NotNull
					@Override
					public StructureViewTreeElement getRoot() {
						return new MyStructureViewElement(module);
					}

					@NotNull
					@Override
					protected Class<?> @NotNull [] getSuitableClasses() {
						throw new UnsupportedOperationException();
					}

					@Override
					protected boolean isSuitable(PsiElement element) {
						return shouldInclude(element);
					}
				};
			}
		};
	}

	public static class MyStructureViewElement implements StructureViewTreeElement, ItemPresentation {

		private final PsiElement element;

		public MyStructureViewElement(PsiElement element) {
			this.element = element;
		}

		@Override
		public Object getValue() {
			return element;
		}

		@NotNull
		@Override
		public ItemPresentation getPresentation() {
			return this;
		}

		@Nullable
		@Override
		public String getPresentableText() {
			IElementType elementType = element.getNode().getElementType();
			if (elementType == Symbols.module && element instanceof ModuleImpl) {
				return "module " + ((ModuleImpl) element).getName();
			} else if (elementType == Symbols.synthetic_List_PortDefinitionGroup && element instanceof CmListImpl<?, ?>) {
				return "interface";
			} else if (elementType == Symbols.implementationItem_SignalLikeDefinitionGroup && element instanceof ImplementationItem_SignalLikeDefinitionGroupImpl) {
				ImplementationItem_SignalLikeDefinitionGroupImpl group = (ImplementationItem_SignalLikeDefinitionGroupImpl) element;
				StringBuilder builder = new StringBuilder();
				builder.append(group.getKindPsi().getText());
				builder.append(' ');
				boolean first = true;
				for (SignalLikeDefinitionImpl definition : group.getDefinitionsPsi().getAllPsi()) {
					if (first) {
						first = false;
					} else {
						builder.append(", ");
					}
					builder.append(definition.getName());
				}
				return builder.toString();
			} else if (elementType == Symbols.implementationItem_DoBlock && element instanceof ImplementationItem_DoBlockImpl) {
				ImplementationItem_DoBlockImpl doBlock = (ImplementationItem_DoBlockImpl) element;
				if (doBlock.getTrigger() == null) {
					return "do (???)";
				} else {
					return "do (" + doBlock.getTriggerPsi().getText() + ")";
				}
			} else if (elementType == Symbols.implementationItem_ModuleInstanceDefinitionGroup && element instanceof ImplementationItem_ModuleInstanceDefinitionGroupImpl) {
				ImplementationItem_ModuleInstanceDefinitionGroupImpl group = (ImplementationItem_ModuleInstanceDefinitionGroupImpl) element;
				StringBuilder builder = new StringBuilder();
				builder.append(group.getModuleName() == null ? "???" : CmUtil.canonicalizeQualifiedModuleName(group.getModuleName()));
				builder.append(' ');
				boolean first = true;
				for (ModuleInstanceDefinitionImpl definition : group.getDefinitionsPsi().getAllPsi()) {
					if (first) {
						first = false;
					} else {
						builder.append(", ");
					}
					builder.append(definition.getName() == null ? "???" : definition.getName());
				}
				return builder.toString();
			} else {
				return element.getText();
			}
		}

		@Nullable
		@Override
		public String getLocationString() {
			return null;
		}

		@Nullable
		@Override
		public Icon getIcon(boolean b) {
			return null;
		}

		@NotNull
		@Override
		public TreeElement @NotNull [] getChildren() {
			List<TreeElement> children = new ArrayList<>();
			checkIncludeChildrenOf(element, children);
			return children.toArray(new TreeElement[0]);
		}

		private static void checkIncludeChildrenOf(PsiElement psiParent, List<TreeElement> destination) {
			for (PsiElement psiChild : psiParent.getChildren()) {
				if (shouldInclude(psiChild)) {
					destination.add(new MyStructureViewElement(psiChild));
				} else {
					checkIncludeChildrenOf(psiChild, destination);
				}
			}
		}

		@Override
		public void navigate(boolean requestFocus) {
			((Navigatable) element).navigate(requestFocus);
		}

		@Override
		public boolean canNavigate() {
			return element instanceof Navigatable && ((Navigatable) element).canNavigate();
		}

		@Override
		public boolean canNavigateToSource() {
			return canNavigate();
		}

	}
}
