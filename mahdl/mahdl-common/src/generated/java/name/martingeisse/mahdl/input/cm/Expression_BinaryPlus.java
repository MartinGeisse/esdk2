package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryPlus extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
