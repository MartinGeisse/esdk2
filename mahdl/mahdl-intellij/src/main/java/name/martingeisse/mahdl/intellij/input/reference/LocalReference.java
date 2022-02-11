/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij.input.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.IncorrectOperationException;
import name.martingeisse.mahdl.input.cm.impl.*;
import name.martingeisse.mahdl.intellij.input.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for local references to signal-likes and module instances. We use a base class that can resolve all of
 * those because they live in the same namespace. Even if a reference targets an object of the wrong kind, we still
 * resolve to that object to make life easier for users who have made a mistake.
 */
public final class LocalReference implements PsiReference {

	private final PsiElement referenceElement;
	private final LeafPsiElement leafElement;

	public LocalReference(PsiElement referenceElement, LeafPsiElement leafElement) {
		this.referenceElement = referenceElement;
		this.leafElement = leafElement;
	}

	@Override
	public @NotNull PsiElement getElement() {
		return referenceElement;
	}

	// note: may only return true for PsiNamedElement objects!
	private boolean isElementTargetable(@NotNull PsiElement potentialTarget) {
		return (potentialTarget instanceof PortDefinitionImpl || potentialTarget instanceof SignalLikeDefinitionImpl ||
			potentialTarget instanceof ModuleInstanceDefinitionImpl);
	}

	@Override
	public @NotNull TextRange getRangeInElement() {
		return new TextRange(0, getCanonicalText().length());
	}

	@Nullable
	@Override
	public PsiElement resolve() {

		ModuleImpl module = PsiUtil.getAncestor(referenceElement, ModuleImpl.class);
		if (module == null) {
			return null;
		}
		String identifier = leafElement.getText();

		// ports
		for (PortDefinitionGroupImpl group : module.getPortDefinitionGroupsPsi().getAllPsi()) {
			if (group instanceof PortDefinitionGroup_ValidImpl) {
				for (PortDefinitionImpl definition : ((PortDefinitionGroup_ValidImpl) group).getDefinitionsPsi().getAllPsi()) {
					String definitionName = definition.getName();
					if (definitionName != null && definitionName.equals(identifier)) {
						return definition;
					}
				}
			}
		}

		// local definitions
		for (ImplementationItemImpl implementationItem : module.getImplementationItemsPsi().getAllPsi()) {
			if (implementationItem instanceof ImplementationItem_SignalLikeDefinitionGroupImpl) {
				for (SignalLikeDefinitionImpl definition : ((ImplementationItem_SignalLikeDefinitionGroupImpl) implementationItem).getDefinitionsPsi().getAllPsi()) {
					String definitionName = definition.getName();
					if (definitionName != null && definitionName.equals(identifier)) {
						return definition;
					}
				}
			} else if (implementationItem instanceof ImplementationItem_ModuleInstanceDefinitionGroupImpl) {
				for (ModuleInstanceDefinitionImpl definition : ((ImplementationItem_ModuleInstanceDefinitionGroupImpl) implementationItem).getDefinitionsPsi().getAllPsi()) {
					String instanceName = definition.getName();
					if (instanceName != null && instanceName.equals(identifier)) {
						return definition.getIdentifierPsi();
					}
				}
			}
		}

		return null;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return leafElement.getText();
	}

	@Override
	public PsiElement handleElementRename(@Nullable String newName) throws IncorrectOperationException {
		if (newName == null) {
			throw new IncorrectOperationException("new name is null");
		}
		return PsiUtil.setText(leafElement, newName);
	}

	@Override
	@NotNull
	public PsiElement bindToElement(@NotNull PsiElement bindTarget) throws IncorrectOperationException {
		if (isElementTargetable(bindTarget)) {
			String newName = ((PsiNamedElement) bindTarget).getName();
			if (newName != null) {
				return PsiUtil.setText(leafElement, newName);
			}
		}
		throw new IncorrectOperationException();
	}

	@Override
	public boolean isReferenceTo(@Nullable PsiElement potentialTarget) {
		if (potentialTarget != null && isElementTargetable(potentialTarget)) {
			String elementName = ((PsiNamedElement) potentialTarget).getName();
			if (elementName != null) {
				String thisName = getCanonicalText();
				if (elementName.equals(thisName)) {
					PsiElement resolved = resolve();
					return (resolved != null && resolved.equals(potentialTarget));
				}
			}
		}
		return false;
	}

	@Override
	public boolean isSoft() {
		return false;
	}

	/**
	 * Common implementation for both identifier expressions and module instance references:
	 * This is useful because of the way auto-complete works in IntelliJ: The IDE inserts the
	 * dummy string "IntellijIdeaRulezzz" at the cursor, re-parses the file, obtains a reference
	 * for the PSI element for that string and calls getVariants() on it. Depending on the context
	 * (especially depending on tokens *after* the cursor), this may yield different kinds of
	 * references unintentionally.
	 */
	@NotNull
	@Override
	public Object @NotNull [] getVariants() {

		// obtain the module
		ModuleImpl module = PsiUtil.getAncestor(referenceElement, ModuleImpl.class);
		if (module == null) {
			return new Object[0];
		}
		List<Object> variants = new ArrayList<>();

		// ports
		for (PortDefinitionGroupImpl group : module.getPortDefinitionGroupsPsi().getAllPsi()) {
			if (group instanceof PortDefinitionGroup_ValidImpl) {
				for (PortDefinitionImpl definition : ((PortDefinitionGroup_ValidImpl) group).getDefinitionsPsi().getAllPsi()) {
					String definitionName = definition.getName();
					if (definitionName != null) {
						variants.add(definitionName);
					}
				}
			}
		}

		// implementation items
		for (ImplementationItemImpl implementationItem : module.getImplementationItemsPsi().getAllPsi()) {
			if (implementationItem instanceof ImplementationItem_SignalLikeDefinitionGroupImpl) {
				for (SignalLikeDefinitionImpl definition : ((ImplementationItem_SignalLikeDefinitionGroupImpl) implementationItem).getDefinitionsPsi().getAllPsi()) {
					String definitionName = definition.getName();
					if (definitionName != null) {
						variants.add(definitionName);
					}
				}
			} else if (implementationItem instanceof ImplementationItem_ModuleInstanceDefinitionGroupImpl) {
				for (ModuleInstanceDefinitionImpl definition : ((ImplementationItem_ModuleInstanceDefinitionGroupImpl) implementationItem).getDefinitionsPsi().getAllPsi()) {
					variants.add(definition.getName());
				}
			}
		}

		return variants.toArray();
	}

}
