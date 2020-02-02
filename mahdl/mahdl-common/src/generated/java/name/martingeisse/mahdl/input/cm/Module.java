package name.martingeisse.mahdl.input.cm;

public interface Module extends CmNode {

	CmOptional<CmToken> getNativeness();

	QualifiedModuleName getModuleName();

	CmList<PortDefinitionGroup> getPortDefinitionGroups();

	CmList<ImplementationItem> getImplementationItems();

}
