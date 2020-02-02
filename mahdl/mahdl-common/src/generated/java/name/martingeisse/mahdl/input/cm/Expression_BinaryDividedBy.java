package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryDividedBy extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
