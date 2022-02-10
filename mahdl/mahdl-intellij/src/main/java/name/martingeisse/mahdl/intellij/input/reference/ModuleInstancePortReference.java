/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.intellij.input.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import name.martingeisse.mahdl.input.cm.impl.*;
import name.martingeisse.mahdl.intellij.input.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ModuleInstancePortReference implements PsiReference {

	private final InstancePortNameImpl instancePortName;

	public ModuleInstancePortReference(@NotNull InstancePortNameImpl instancePortName) {
		this.instancePortName = instancePortName;
	}

	@Override
	@NotNull
	public PsiElement getElement() {
		return instancePortName;
	}

	@Override
	@NotNull
	public TextRange getRangeInElement() {
		return new TextRange(0, getCanonicalText().length());
	}

	@Nullable
	private PsiElement resolveModule() {
		Expression_InstancePortImpl expression = PsiUtil.getAncestor(instancePortName, Expression_InstancePortImpl.class);
		if (expression == null) {
			return null;
		}
		PsiElement someElementInsideInstanceDefinition = expression.getInstanceNamePsi().getReference().resolve();
		if (someElementInsideInstanceDefinition == null) {
			return null;
		}
		ImplementationItem_ModuleInstanceDefinitionGroupImpl moduleInstanceDefinitionGroup = PsiUtil.getAncestor(someElementInsideInstanceDefinition, ImplementationItem_ModuleInstanceDefinitionGroupImpl.class);
		if (moduleInstanceDefinitionGroup == null) {
			// at least resolve to inside the instance
			return someElementInsideInstanceDefinition;
		}
		PsiElement moduleNameDefiningElement = moduleInstanceDefinitionGroup.getModuleNamePsi().getReference().resolve();
		if (moduleNameDefiningElement == null) {
			// the module name is unknown
			return moduleInstanceDefinitionGroup.getModuleNamePsi();
		}
		ModuleImpl module = PsiUtil.getAncestor(moduleNameDefiningElement, ModuleImpl.class);
		if (module == null) {
			// the module name defining element is lost in a PSI soup
			return moduleNameDefiningElement;
		}
		return module;
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		PsiElement resolvedModule = resolveModule();
		if (!(resolvedModule instanceof ModuleImpl)) {
			return resolvedModule;
		}
		ModuleImpl targetModule = (ModuleImpl) resolvedModule;
		String referencePortName = getCanonicalText();
		for (PortDefinitionGroupImpl portDefinitionGroup : targetModule.getPortDefinitionGroupsPsi().getAllPsi()) {
			if (portDefinitionGroup instanceof PortDefinitionGroup_ValidImpl) {
				for (PortDefinitionImpl portDefinition : ((PortDefinitionGroup_ValidImpl) portDefinitionGroup).getDefinitionsPsi().getAllPsi()) {
					String definitionPortName = portDefinition.getName();
					if (referencePortName.equals(definitionPortName)) {
						return portDefinition;
					}
				}
			}
		}
		// we found a module, but that module doesn't have a matching port. At least resolve to the module.
		return targetModule;
	}

	// Works similar to resolve(), but won't return anything other than a PortDefinition. That is, any failure case
	// doesn't resolve the reference "as good as we can" but just returns null.
	@Nullable
	public PortDefinitionImpl resolvePortDefinitionOnly() {
		PsiElement element = resolve();
		return (element instanceof PortDefinitionImpl ? (PortDefinitionImpl) element : null);
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return instancePortName.getIdentifierPsi().getText();
	}

	@Override
	@NotNull
	public PsiElement handleElementRename(@Nullable String newName) throws IncorrectOperationException {
		if (newName == null) {
			throw new IncorrectOperationException("new name is null");
		}
		return PsiUtil.setText(instancePortName.getIdentifierPsi(), newName);
	}

	@Override
	@NotNull
	public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
		if (psiElement instanceof PortDefinitionImpl) {
			String newName = ((PsiNamedElement) psiElement).getName();
			if (newName != null) {
				return PsiUtil.setText(instancePortName.getIdentifierPsi(), newName);
			}
		}
		throw new IncorrectOperationException();
	}

	@Override
	public boolean isReferenceTo(@Nullable PsiElement psiElement) {
		if (psiElement instanceof PortDefinitionImpl) {
			String candidatePortName = ((PortDefinitionImpl) psiElement).getName();
			if (candidatePortName != null && candidatePortName.equals(getCanonicalText())) {
				PsiElement resolved = resolve();
				return (resolved != null && resolved.equals(psiElement));
			}
		}
		return false;
	}

	@NotNull
	@Override
	public Object @NotNull [] getVariants() {
		// note: if this returns PSI elements, they must be PsiNamedElement or contain the name in meta-data
		List<String> portNames = new ArrayList<>();
		PsiElement resolvedModule = resolveModule();
		if (resolvedModule instanceof ModuleImpl) {
			ModuleImpl targetModule = (ModuleImpl) resolvedModule;
			for (PortDefinitionGroupImpl portDefinitionGroup : targetModule.getPortDefinitionGroupsPsi().getAllPsi()) {
				if (portDefinitionGroup instanceof PortDefinitionGroup_ValidImpl) {
					for (PortDefinitionImpl portDefinition : ((PortDefinitionGroup_ValidImpl) portDefinitionGroup).getDefinitionsPsi().getAllPsi()) {
						String definitionPortName = portDefinition.getName();
						if (definitionPortName != null) {
							portNames.add(definitionPortName);
						}
					}
				}
			}
		}
		return portNames.toArray();
	}

	@Override
	public boolean isSoft() {
		return false;
	}

}
