package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;

public final class ImplementationItem_SignalLikeDefinitionGroupImpl extends ImplementationItemImpl implements ImplementationItem_SignalLikeDefinitionGroup, PsiCm {

	public ImplementationItem_SignalLikeDefinitionGroupImpl(@NotNull ASTNode node) {
		super(node);
	}

	public SignalLikeKind getKind() {
		return (SignalLikeKind) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public SignalLikeKindImpl getKindPsi() {
		return (SignalLikeKindImpl) InternalPsiUtil.getChild(this, 0);
	}

	public DataType getDataType() {
		return (DataType) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public DataTypeImpl getDataTypePsi() {
		return (DataTypeImpl) InternalPsiUtil.getChild(this, 1);
	}

	public CmList<SignalLikeDefinition> getDefinitions() {
		return (CmList<SignalLikeDefinition>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public CmListImpl<SignalLikeDefinition, SignalLikeDefinitionImpl> getDefinitionsPsi() {
		return (CmListImpl<SignalLikeDefinition, SignalLikeDefinitionImpl>) InternalPsiUtil.getChild(this, 2);
	}

}
