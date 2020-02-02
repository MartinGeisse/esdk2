package name.martingeisse.mahdl.input.cm;

public interface Expression_FunctionCall extends Expression {

	FunctionName getFunctionName();

	CmList<Expression> getArguments();

}
