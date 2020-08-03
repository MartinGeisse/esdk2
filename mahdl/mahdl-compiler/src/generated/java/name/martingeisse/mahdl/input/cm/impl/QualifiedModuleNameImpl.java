package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;

public final class QualifiedModuleNameImpl extends CmNodeImpl implements QualifiedModuleName {

	private final CmList<CmToken> segments;

	public QualifiedModuleNameImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.segments = (CmList<CmToken>) childNodes[0];
		((CmNodeImpl) this.segments).setParent(this);
	}

	public CmList<CmToken> getSegments() {
		return segments;
	}

}
