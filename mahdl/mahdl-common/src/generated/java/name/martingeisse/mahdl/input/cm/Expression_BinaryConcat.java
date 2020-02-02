package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryConcat extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
