package name.martingeisse.mahdl.input.cm;

public interface Statement_IfThenElse extends Statement {

	Expression getCondition();

	Statement getThenBranch();

	Statement getElseBranch();

}
