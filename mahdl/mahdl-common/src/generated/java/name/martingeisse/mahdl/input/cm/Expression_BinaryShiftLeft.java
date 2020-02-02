package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryShiftLeft extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
