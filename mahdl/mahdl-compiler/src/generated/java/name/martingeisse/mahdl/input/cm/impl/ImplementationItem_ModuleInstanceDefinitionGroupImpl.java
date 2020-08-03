package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.ImplementationItem_ModuleInstanceDefinitionGroup;
import name.martingeisse.mahdl.input.cm.ModuleInstanceDefinition;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;

public final class ImplementationItem_ModuleInstanceDefinitionGroupImpl extends ImplementationItemImpl implements ImplementationItem_ModuleInstanceDefinitionGroup {

	private final QualifiedModuleName moduleName;
	private final CmList<ModuleInstanceDefinition> definitions;

	public ImplementationItem_ModuleInstanceDefinitionGroupImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.moduleName = (QualifiedModuleName) childNodes[0];
		((CmNodeImpl) this.moduleName).setParent(this);
		this.definitions = (CmList<ModuleInstanceDefinition>) childNodes[1];
		((CmNodeImpl) this.definitions).setParent(this);
	}

	public QualifiedModuleName getModuleName() {
		return moduleName;
	}

	public CmList<ModuleInstanceDefinition> getDefinitions() {
		return definitions;
	}

}
