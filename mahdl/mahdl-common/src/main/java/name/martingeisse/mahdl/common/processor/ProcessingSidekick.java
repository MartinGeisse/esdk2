package name.martingeisse.mahdl.common.processor;

import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.UnknownExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmLinked;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public final class ProcessingSidekick {

	public static final ProcessingSidekick ISOLATED = new ProcessingSidekick(ErrorHandler.NOP);

	private final ErrorHandler errorHandler;

	public ProcessingSidekick(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void onError(@NotNull CmLinked errorSource, @NotNull String message) {
		errorHandler.onError(errorSource, message);
	}

	public void onError(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
		errorHandler.onError(errorSource, message, t);
	}

	public void onDiagnostic(@NotNull CmLinked errorSource, @NotNull String message) {
		errorHandler.onDiagnostic(errorSource, message);
	}

	public void onDiagnostic(@NotNull CmLinked errorSource, @NotNull String message, @Nullable Throwable t) {
		errorHandler.onDiagnostic(errorSource, message, t);
	}

	public final ProcessedExpression expectType(ProcessedExpression expression, ProcessedDataType.Family expectedFamily) {
		ProcessedDataType.Family actualFamily = expression.getDataType().getFamily();
		if (actualFamily == ProcessedDataType.Family.UNKNOWN) {
			onDiagnostic(expression, "unknown type should have been " + expectedFamily);
			return expression;
		}
		if (expectedFamily != actualFamily) {
			onError(expression, "expected type " + expectedFamily + ", found " + expression.getDataType());
			return new UnknownExpression(expression.getErrorSource());
		}
		return expression;
	}

	public final ProcessedExpression expectType(ProcessedExpression expression, ProcessedDataType expectedType) {
		ProcessedDataType actualType = expression.getDataType();
		if (actualType.getFamily() == ProcessedDataType.Family.UNKNOWN) {
			onDiagnostic(expression, "unknown type should have been " + expectedType);
			return expression;
		}
		if (!expectedType.equals(actualType)) {
			onError(expression, "expected type " + expectedType + ", found " + actualType);
			return new UnknownExpression(expression.getErrorSource());
		}
		return expression;
	}

}
