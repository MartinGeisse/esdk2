package name.martingeisse.esdk.core.model.validation;

import com.google.common.collect.ImmutableSortedMap;

import java.util.Map;

/**
 *
 */
public final class DesignValidationResult {

	private final ImmutableSortedMap<String, ItemValidationResult> toplevelItemResults;

	public DesignValidationResult(ImmutableSortedMap<String, ItemValidationResult> toplevelItemResults) {
		this.toplevelItemResults = toplevelItemResults;
	}

	public ImmutableSortedMap<String, ItemValidationResult> getToplevelItemResults() {
		return toplevelItemResults;
	}

	public void print(ValidationResultPrinter printer) {
		for (Map.Entry<String, ItemValidationResult> result : toplevelItemResults.entrySet()) {
			result.getValue().print(printer, result.getKey());
		}
	}

}
