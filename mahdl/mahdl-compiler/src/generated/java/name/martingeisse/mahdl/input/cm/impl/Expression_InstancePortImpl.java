package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression_InstancePort;
import name.martingeisse.mahdl.input.cm.InstancePortName;
import name.martingeisse.mahdl.input.cm.InstanceReferenceName;

public final class Expression_InstancePortImpl extends ExpressionImpl implements Expression_InstancePort {

	private final InstanceReferenceName instanceName;
	private final InstancePortName portName;

	public Expression_InstancePortImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.instanceName = (InstanceReferenceName) childNodes[0];
		((CmNodeImpl) this.instanceName).setParent(this);
		this.portName = (InstancePortName) childNodes[2];
		((CmNodeImpl) this.portName).setParent(this);
	}

	public InstanceReferenceName getInstanceName() {
		return instanceName;
	}

	public InstancePortName getPortName() {
		return portName;
	}

}
