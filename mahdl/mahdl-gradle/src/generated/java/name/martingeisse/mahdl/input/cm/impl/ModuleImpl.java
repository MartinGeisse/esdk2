package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.*;

public final class ModuleImpl extends CmNodeImpl implements Module {

	private final CmOptional<CmToken> nativeness;
	private final QualifiedModuleName moduleName;
	private final CmList<PortDefinitionGroup> portDefinitionGroups;
	private final CmList<ImplementationItem> implementationItems;

	public ModuleImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.nativeness = (CmOptional<CmToken>) childNodes[0];
		((CmNodeImpl) this.nativeness).setParent(this);
		this.moduleName = (QualifiedModuleName) childNodes[2];
		((CmNodeImpl) this.moduleName).setParent(this);
		this.portDefinitionGroups = (CmList<PortDefinitionGroup>) childNodes[6];
		((CmNodeImpl) this.portDefinitionGroups).setParent(this);
		this.implementationItems = (CmList<ImplementationItem>) childNodes[8];
		((CmNodeImpl) this.implementationItems).setParent(this);
	}

	public CmOptional<CmToken> getNativeness() {
		return nativeness;
	}

	public QualifiedModuleName getModuleName() {
		return moduleName;
	}

	public CmList<PortDefinitionGroup> getPortDefinitionGroups() {
		return portDefinitionGroups;
	}

	public CmList<ImplementationItem> getImplementationItems() {
		return implementationItems;
	}

}
