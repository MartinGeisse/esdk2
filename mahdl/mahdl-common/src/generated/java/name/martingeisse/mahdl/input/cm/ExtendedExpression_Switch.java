package name.martingeisse.mahdl.input.cm;

public interface ExtendedExpression_Switch extends ExtendedExpression {

	Expression getSelector();

	CmList<ExpressionCaseItem> getItems();

}
