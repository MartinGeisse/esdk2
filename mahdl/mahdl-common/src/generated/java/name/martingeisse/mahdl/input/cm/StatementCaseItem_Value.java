package name.martingeisse.mahdl.input.cm;

public interface StatementCaseItem_Value extends StatementCaseItem {

	CmList<Expression> getSelectorValues();

	CmList<Statement> getStatements();

}
