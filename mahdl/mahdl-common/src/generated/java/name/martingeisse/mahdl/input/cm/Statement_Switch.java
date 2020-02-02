package name.martingeisse.mahdl.input.cm;

public interface Statement_Switch extends Statement {

	Expression getSelector();

	CmList<StatementCaseItem> getItems();

}
