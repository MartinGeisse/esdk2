package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryLessThanOrEqual extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
