package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryShiftRight extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
