package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryAnd extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
