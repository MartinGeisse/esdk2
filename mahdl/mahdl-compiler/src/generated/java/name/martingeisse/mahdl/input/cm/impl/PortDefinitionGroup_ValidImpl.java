package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.*;

public final class PortDefinitionGroup_ValidImpl extends PortDefinitionGroupImpl implements PortDefinitionGroup_Valid {

	private final PortDirection direction;
	private final DataType dataType;
	private final CmList<PortDefinition> definitions;

	public PortDefinitionGroup_ValidImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.direction = (PortDirection) childNodes[0];
		((CmNodeImpl) this.direction).setParent(this);
		this.dataType = (DataType) childNodes[1];
		((CmNodeImpl) this.dataType).setParent(this);
		this.definitions = (CmList<PortDefinition>) childNodes[2];
		((CmNodeImpl) this.definitions).setParent(this);
	}

	public PortDirection getDirection() {
		return direction;
	}

	public DataType getDataType() {
		return dataType;
	}

	public CmList<PortDefinition> getDefinitions() {
		return definitions;
	}

}
