package name.martingeisse.mahdl.input.cm;

public interface Expression_BinaryRemainder extends Expression, BinaryOperation {

	Expression getLeftOperand();

	Expression getRightOperand();

}
