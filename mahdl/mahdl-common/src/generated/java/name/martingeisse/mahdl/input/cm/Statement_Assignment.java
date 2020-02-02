package name.martingeisse.mahdl.input.cm;

public interface Statement_Assignment extends Statement {

	Expression getLeftSide();

	ExtendedExpression getRightSide();

}
