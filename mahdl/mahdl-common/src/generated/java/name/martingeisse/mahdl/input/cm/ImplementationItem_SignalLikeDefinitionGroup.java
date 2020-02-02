package name.martingeisse.mahdl.input.cm;

public interface ImplementationItem_SignalLikeDefinitionGroup extends ImplementationItem {

	SignalLikeKind getKind();

	DataType getDataType();

	CmList<SignalLikeDefinition> getDefinitions();

}
