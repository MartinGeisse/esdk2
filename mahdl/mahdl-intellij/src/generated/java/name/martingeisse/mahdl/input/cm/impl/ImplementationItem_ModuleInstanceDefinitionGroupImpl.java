package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.ImplementationItem_ModuleInstanceDefinitionGroup;
import name.martingeisse.mahdl.input.cm.ModuleInstanceDefinition;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import org.jetbrains.annotations.NotNull;

public final class ImplementationItem_ModuleInstanceDefinitionGroupImpl extends ImplementationItemImpl implements ImplementationItem_ModuleInstanceDefinitionGroup, PsiCm {

	public ImplementationItem_ModuleInstanceDefinitionGroupImpl(@NotNull ASTNode node) {
		super(node);
	}

	public QualifiedModuleName getModuleName() {
		return (QualifiedModuleName) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public QualifiedModuleNameImpl getModuleNamePsi() {
		return (QualifiedModuleNameImpl) InternalPsiUtil.getChild(this, 0);
	}

	public CmList<ModuleInstanceDefinition> getDefinitions() {
		return (CmList<ModuleInstanceDefinition>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public CmListImpl<ModuleInstanceDefinition, ModuleInstanceDefinitionImpl> getDefinitionsPsi() {
		return (CmListImpl<ModuleInstanceDefinition, ModuleInstanceDefinitionImpl>) InternalPsiUtil.getChild(this, 1);
	}

}
