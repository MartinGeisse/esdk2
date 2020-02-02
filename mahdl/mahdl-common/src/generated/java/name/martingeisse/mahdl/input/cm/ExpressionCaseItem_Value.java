package name.martingeisse.mahdl.input.cm;

public interface ExpressionCaseItem_Value extends ExpressionCaseItem {

	CmList<Expression> getSelectorValues();

	ExtendedExpression getResultValue();

}
