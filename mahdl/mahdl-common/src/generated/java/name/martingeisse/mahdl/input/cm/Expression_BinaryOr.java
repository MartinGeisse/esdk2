package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryOr extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
