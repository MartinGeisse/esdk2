package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryTimes extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
