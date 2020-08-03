package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmNode;

import java.util.concurrent.atomic.AtomicInteger;

public class IElementType {

	private static final AtomicInteger INDEX_ALLOCATOR = new AtomicInteger();
	public static final IElementType BAD_CHARACTER = new IElementType("%badchar", null);
	public static final IElementType WHITE_SPACE = new IElementType("%whitespace", null);
	private final int index;
	private final String debugName;
	private final CmNodeFactory cmNodeFactory;

	public IElementType(String debugName, CmNodeFactory cmNodeFactory) {
		this.index = INDEX_ALLOCATOR.getAndIncrement();
		this.debugName = debugName;
		this.cmNodeFactory = cmNodeFactory;
	}

	public int getIndex() {
		return index;
	}

	public String getDebugName() {
		return debugName;
	}

	public CmNodeFactory getCmNodeFactory() {
		return cmNodeFactory;
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public String toString() {
		return debugName;
	}

	public interface CmNodeFactory {
		CmNode createCmNode(int row, int column, Object[] childNodes);
	}

}
