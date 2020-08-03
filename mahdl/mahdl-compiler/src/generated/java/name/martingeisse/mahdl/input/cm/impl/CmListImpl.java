package name.martingeisse.mahdl.input.cm.impl;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.CmNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class CmListImpl<T extends CmNode> extends CmNodeImpl implements CmList<T> {

	private final Class<T> elementClass;
	private final List<T> elements;

	public CmListImpl(int row, int column, Class<T> elementClass, Object[] childNodes) {
		super(row, column);
		this.elementClass = elementClass;
		this.elements = new ArrayList<>();
		for (Object childNode : childNodes) {
			T typedChildNode = elementClass.cast(childNode);
			elements.add(typedChildNode);
			((CmNodeImpl) typedChildNode).setParent(this);
		}
	}

	public <S extends CmNode> CmListImpl<S> cast(Class<S> subclass) {
		if (!elementClass.isAssignableFrom(subclass)) {
			throw new ClassCastException(subclass.getName() + " is not a subclass of " + elementClass.getName());
		}
		return (CmListImpl) this;
	}

	public final List<T> getAll() {
		List<T> list = new ArrayList<>();
		addAllTo(list);
		return list;
	}

	public final void addAllTo(List<T> list) {
		foreach(list::add);
	}

	public final void addAllTo(ImmutableList.Builder<T> builder) {
		foreach(builder::add);
	}

	public final void foreach(Consumer<T> consumer) {
		for (T element : elements) {
			consumer.accept(element);
		}
	}

}
