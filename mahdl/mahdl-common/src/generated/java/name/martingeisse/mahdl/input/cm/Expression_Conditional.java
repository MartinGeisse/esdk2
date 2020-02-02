package name.martingeisse.mahdl.input.cm;

public interface Expression_Conditional extends Expression {

	Expression getCondition();

	Expression getThenBranch();

	Expression getElseBranch();

}
