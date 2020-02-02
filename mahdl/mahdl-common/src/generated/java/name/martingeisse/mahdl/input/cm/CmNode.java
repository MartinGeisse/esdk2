package name.martingeisse.mahdl.input.cm;

public interface CmNode extends CmLinked {

	default CmNode getCmNode() {
		return this;
	}

	CmNode getCmParent();

	int compareStartOffset(CmNode other);

}
