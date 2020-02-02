package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.IncorrectOperationException;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;

public final class ModuleImpl extends ASTWrapperPsiElement implements Module, PsiCm, PsiNamedElement, PsiNameIdentifierOwner {

	public ModuleImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmOptional<CmToken> getNativeness() {
		return (CmOptional<CmToken>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public CmOptionalImpl<CmToken, LeafPsiElement> getNativenessPsi() {
		return (CmOptionalImpl<CmToken, LeafPsiElement>) InternalPsiUtil.getChild(this, 0);
	}

	public QualifiedModuleName getModuleName() {
		return (QualifiedModuleName) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public QualifiedModuleNameImpl getModuleNamePsi() {
		return (QualifiedModuleNameImpl) InternalPsiUtil.getChild(this, 2);
	}

	public CmList<PortDefinitionGroup> getPortDefinitionGroups() {
		return (CmList<PortDefinitionGroup>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 6));
	}

	public CmListImpl<PortDefinitionGroup, PortDefinitionGroupImpl> getPortDefinitionGroupsPsi() {
		return (CmListImpl<PortDefinitionGroup, PortDefinitionGroupImpl>) InternalPsiUtil.getChild(this, 6);
	}

	public CmList<ImplementationItem> getImplementationItems() {
		return (CmList<ImplementationItem>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 8));
	}

	public CmListImpl<ImplementationItem, ImplementationItemImpl> getImplementationItemsPsi() {
		return (CmListImpl<ImplementationItem, ImplementationItemImpl>) InternalPsiUtil.getChild(this, 8);
	}

	public String getName() {
		return name.martingeisse.mahdl.intellij.input.PsiUtil.getName(this);
	}

	public PsiElement setName(String newName) throws IncorrectOperationException {
		return name.martingeisse.mahdl.intellij.input.PsiUtil.setName(this, newName);
	}

	public PsiElement getNameIdentifier() {
		return name.martingeisse.mahdl.intellij.input.PsiUtil.getNameIdentifier(this);
	}

	public void superclassDelete() throws IncorrectOperationException {
		super.delete();
	}

	public void delete() throws IncorrectOperationException {
		name.martingeisse.mahdl.intellij.input.PsiUtil.delete(this);
	}

}
