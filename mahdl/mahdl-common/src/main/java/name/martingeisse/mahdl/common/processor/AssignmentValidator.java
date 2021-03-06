/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor;

import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.processor.definition.*;
import name.martingeisse.mahdl.common.processor.expression.*;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.PortDirection_Out;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This object detects assignments to invalid targets such as constants or operator expressions (except assignments to
 * concatenation, which is allowed). It also detects multiple or missing assignments to signals and registers. It is NOT
 * concerned with type safety and assumes that the {@link ExpressionProcessor} has detected any type errors already.
 */
public final class AssignmentValidator {

	private final ProcessingSidekick sidekick;
	private final Set<String> previouslyAssignedSignals = new HashSet<>();
	private final Set<String> newlyAssignedSignals = new HashSet<>();

	public AssignmentValidator(@NotNull ProcessingSidekick sidekick) {
		this.sidekick = sidekick;
	}

	public void finishSection() {
		previouslyAssignedSignals.addAll(newlyAssignedSignals);
		newlyAssignedSignals.clear();
	}

	public void checkMissingAssignments(@NotNull Collection<Named> definitions) {
		for (Named definition : definitions) {
			if (definition instanceof ModulePort) {
				ModulePort port = (ModulePort) definition;
				if (port.getDirectionElement() instanceof PortDirection_Out) {
					if (port.getInitializer() == null && !previouslyAssignedSignals.contains(port.getName())) {
						sidekick.onError(port.getNameElement(), "missing assignment for port '" + port.getName() + "'");
					}
				}
			} else if (definition instanceof Signal) {
				Signal signal = (Signal) definition;
				if (signal.getInitializer() == null && !previouslyAssignedSignals.contains(signal.getName())) {
					sidekick.onError(signal.getNameElement(), "missing assignment for signal '" + signal.getName() + "'");
				}
			} else if (definition instanceof ModuleInstance) {
				ModuleInstance moduleInstance = (ModuleInstance) definition;
				String instanceName = moduleInstance.getName();
				for (ModuleApi.Port portApi : moduleInstance.getModuleApi().getPorts()) {
					if (portApi.getDirection() == PortDirection.IN) {
						String prefixedPortName = instanceName + '.' + portApi.getName();
						if (!previouslyAssignedSignals.contains(prefixedPortName)) {
							sidekick.onError(moduleInstance.getModuleInstanceDefinitionElement(),
									"missing assignment for port '" + portApi.getName() + "' in instance '" + instanceName + "'");
						}
					}
				}
			}
		}
	}

	public void validateAssignmentTo(@NotNull ProcessedExpression destination, TriggerKind triggerKind) {
		// noinspection ConstantConditions
		if (destination == null) {
			throw new IllegalArgumentException("destination cannot be null");
		}
		if (destination instanceof ProcessedConstantExpression) {

			sidekick.onError(destination.getErrorSource(), "cannot assign to a constant");

		} else if (destination instanceof SignalLikeReference) {

			SignalLike signalLike = ((SignalLikeReference) destination).getDefinition();
			CmNode errorSource = destination.getErrorSource();
			if (signalLike instanceof ModulePort) {
				PortDirection direction = ((ModulePort) signalLike).getDirection();
				if (direction != PortDirection.OUT) {
					sidekick.onError(errorSource, "input port " + signalLike.getName() + " cannot be assigned to");
				} else if (triggerKind != TriggerKind.CONTINUOUS) {
					sidekick.onError(errorSource, "assignment to module port must be continuous");
				}
			} else if (signalLike instanceof Signal) {
				if (triggerKind != TriggerKind.CONTINUOUS) {
					sidekick.onError(errorSource, "assignment to signal must be continuous");
				}
			} else if (signalLike instanceof Register) {
				if (triggerKind != TriggerKind.CLOCKED) {
					sidekick.onError(errorSource, "assignment to register must be clocked");
				}
			} else if (signalLike instanceof Constant) {
				sidekick.onError(errorSource, "cannot assign to constant");
			}
			considerAssignedTo(signalLike, destination.getErrorSource());

		} else if (destination instanceof ProcessedIndexSelection) {

			validateAssignmentTo(((ProcessedIndexSelection) destination).getContainer(), triggerKind);

		} else if (destination instanceof ProcessedRangeSelection) {

			validateAssignmentTo(((ProcessedRangeSelection) destination).getContainer(), triggerKind);

		} else if (destination instanceof ProcessedBinaryOperation) {

			ProcessedBinaryOperation binaryOperation = (ProcessedBinaryOperation) destination;
			if (binaryOperation.getOperator() == ProcessedBinaryOperator.VECTOR_CONCAT) {
				validateAssignmentTo(binaryOperation.getLeftOperand(), triggerKind);
				validateAssignmentTo(binaryOperation.getRightOperand(), triggerKind);
			} else {
				sidekick.onError(destination.getErrorSource(), "expression cannot be assigned to");
			}

		} else if (destination instanceof InstancePortReference) {

			InstancePortReference instancePortReference = (InstancePortReference) destination;
			if (triggerKind != TriggerKind.CONTINUOUS) {
				sidekick.onError(destination.getErrorSource(), "assignment to instance port must be continuous");
			}
			validateAssignmentToInstancePort(instancePortReference.getModuleInstance(), instancePortReference.getPort(),
				instancePortReference.getErrorSource());

		} else if (!(destination instanceof UnknownExpression)) {

			sidekick.onError(destination.getErrorSource(), "expression cannot be assigned to");

		}
	}

	/**
	 * Remembers the specified signal-like as "assigned to" in the current "section" (definition or do-block).
	 * <p>
	 * Does not check whether the signal-like can be assigned to in this context -- e.g. it will not treat an assignment
	 * to a constant as an error. It just checks whether this assignment is in conflict with another assignment.
	 * <p>
	 * Therefore, this method should not be called for the initializer of a register, because that initializer is not
	 * in conflict with an assignment to the register, but calling this function would assume it to be.
	 */
	public void considerAssignedTo(@NotNull SignalLike signalLike, @NotNull CmNode errorSource) {
		considerAssignedTo(signalLike.getName(), errorSource);
	}

	public void validateAssignmentToInstancePort(@NotNull ModuleInstance moduleInstance, @NotNull InstancePort port, @NotNull CmNode errorSource) {
		if (port.getDirection() == PortDirection.OUT) {
			sidekick.onError(errorSource, "cannot assign to output port");
		} else {
			considerAssignedTo(moduleInstance.getName() + '.' + port.getName(), errorSource);
		}
	}

	private void considerAssignedTo(@NotNull String signalName, @NotNull CmNode errorSource) {
		if (previouslyAssignedSignals.contains(signalName)) {
			sidekick.onError(errorSource, "'" + signalName + "' has already been assigned to");
		}
		newlyAssignedSignals.add(signalName);
	}

	public enum TriggerKind {
		CONTINUOUS, CLOCKED
	}

}
