package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;

public final class PortDefinitionGroup_ValidImpl extends PortDefinitionGroupImpl implements PortDefinitionGroup_Valid, PsiCm {

	public PortDefinitionGroup_ValidImpl(@NotNull ASTNode node) {
		super(node);
	}

	public PortDirection getDirection() {
		return (PortDirection) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public PortDirectionImpl getDirectionPsi() {
		return (PortDirectionImpl) InternalPsiUtil.getChild(this, 0);
	}

	public DataType getDataType() {
		return (DataType) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public DataTypeImpl getDataTypePsi() {
		return (DataTypeImpl) InternalPsiUtil.getChild(this, 1);
	}

	public CmList<PortDefinition> getDefinitions() {
		return (CmList<PortDefinition>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public CmListImpl<PortDefinition, PortDefinitionImpl> getDefinitionsPsi() {
		return (CmListImpl<PortDefinition, PortDefinitionImpl>) InternalPsiUtil.getChild(this, 2);
	}

}
