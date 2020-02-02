package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression_InstancePort;
import name.martingeisse.mahdl.input.cm.InstancePortName;
import name.martingeisse.mahdl.input.cm.InstanceReferenceName;
import org.jetbrains.annotations.NotNull;

public final class Expression_InstancePortImpl extends ExpressionImpl implements Expression_InstancePort, PsiCm {

	public Expression_InstancePortImpl(@NotNull ASTNode node) {
		super(node);
	}

	public InstanceReferenceName getInstanceName() {
		return (InstanceReferenceName) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public InstanceReferenceNameImpl getInstanceNamePsi() {
		return (InstanceReferenceNameImpl) InternalPsiUtil.getChild(this, 0);
	}

	public InstancePortName getPortName() {
		return (InstancePortName) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public InstancePortNameImpl getPortNamePsi() {
		return (InstancePortNameImpl) InternalPsiUtil.getChild(this, 2);
	}

}
