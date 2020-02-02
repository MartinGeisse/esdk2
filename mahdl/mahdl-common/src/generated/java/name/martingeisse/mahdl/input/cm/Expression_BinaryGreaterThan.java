package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryGreaterThan extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
