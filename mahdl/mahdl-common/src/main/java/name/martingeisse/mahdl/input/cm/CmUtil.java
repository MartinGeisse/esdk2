package name.martingeisse.mahdl.input.cm;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class CmUtil {

	// prevent instantiation
	private CmUtil() {
	}

	@NotNull
	public static String canonicalizeQualifiedModuleName(@NotNull QualifiedModuleName name) {
		return StringUtils.join(parseQualifiedModuleName(name), '.');
	}

	@NotNull
	public static String[] parseQualifiedModuleName(@NotNull QualifiedModuleName name) {
		List<String> segments = new ArrayList<>();
		for (CmToken segment : name.getSegments().getAll()) {
			segments.add(segment.getText());
		}
		return segments.toArray(new String[segments.size()]);
	}

	@Nullable
	public static <T> T getAncestor(@NotNull CmNode node, @NotNull Class<T> nodeClass) {
		while (true) {
			if (nodeClass.isInstance(node)) {
				return nodeClass.cast(node);
			}
			if (node == null) {
				return null;
			}
			node = node.getCmParent();
		}
	}

}
