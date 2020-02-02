package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryXor extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
