package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryEqual extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
