package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_FunctionCall;
import name.martingeisse.mahdl.input.cm.FunctionName;

public final class Expression_FunctionCallImpl extends ExpressionImpl implements Expression_FunctionCall {

	private final FunctionName functionName;
	private final CmList<Expression> arguments;

	public Expression_FunctionCallImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.functionName = (FunctionName) childNodes[0];
		((CmNodeImpl) this.functionName).setParent(this);
		this.arguments = (CmList<Expression>) childNodes[2];
		((CmNodeImpl) this.arguments).setParent(this);
	}

	public FunctionName getFunctionName() {
		return functionName;
	}

	public CmList<Expression> getArguments() {
		return arguments;
	}

}
