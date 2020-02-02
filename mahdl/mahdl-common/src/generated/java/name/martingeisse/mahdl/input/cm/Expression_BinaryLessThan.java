package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryLessThan extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
