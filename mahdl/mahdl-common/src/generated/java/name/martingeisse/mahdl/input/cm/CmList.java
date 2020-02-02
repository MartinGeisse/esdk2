package name.martingeisse.mahdl.input.cm;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Consumer;

public interface CmList<T extends CmNode> extends CmNode {

	<S extends CmNode> CmList<S> cast(Class<S> subclass);

	List<T> getAll();

	void addAllTo(List<T> list);

	void addAllTo(ImmutableList.Builder<T> builder);

	void foreach(Consumer<T> consumer);

}
