package name.martingeisse.mahdl.input.cm;

public interface ImplementationItem_ModuleInstanceDefinitionGroup extends ImplementationItem {

	QualifiedModuleName getModuleName();

	CmList<ModuleInstanceDefinition> getDefinitions();

}
