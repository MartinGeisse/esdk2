package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryNotEqual extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
