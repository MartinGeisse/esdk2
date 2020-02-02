package name.martingeisse.mahdl.input.cm;

public interface Statement_IfThen extends Statement {

	Expression getCondition();

	Statement getThenBranch();

}
