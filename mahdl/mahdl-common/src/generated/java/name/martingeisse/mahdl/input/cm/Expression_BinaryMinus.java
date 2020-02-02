package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryMinus extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
