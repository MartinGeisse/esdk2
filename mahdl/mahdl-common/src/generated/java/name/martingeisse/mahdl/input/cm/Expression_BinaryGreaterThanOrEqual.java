package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryGreaterThanOrEqual extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
