package name.martingeisse.esdk.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class TransformationOutputMap {

	private final Map<Pair<Object, Key<?, ?>>, Object> map = new HashMap<>();

	public <A, B> void put(A originalObject, Key<A, B> key, B transformedObject) {
		map.put(Pair.of(originalObject, key), transformedObject);
	}

	@SuppressWarnings("unchecked")
	public <A, B> B get(A originalObject, Key<A, B> key) {
		Pair pair = Pair.of(originalObject, key);
		Object untyped = map.get(pair);
		return (B)untyped;
	}

}
