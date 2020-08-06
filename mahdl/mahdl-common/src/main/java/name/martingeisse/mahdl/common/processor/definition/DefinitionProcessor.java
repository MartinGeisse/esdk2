/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.common.Environment;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ReferenceResolutionException;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.type.DataTypeProcessor;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class DefinitionProcessor {

	@NotNull
	private final ProcessingSidekick sidekick;

	@NotNull
	private final DataTypeProcessor dataTypeProcessor;

	@NotNull
	private final ExpressionProcessor expressionProcessor;

	@NotNull
	private final Map<String, Named> definitions;

	public DefinitionProcessor(@NotNull ProcessingSidekick sidekick,
							   @NotNull DataTypeProcessor dataTypeProcessor,
							   @NotNull ExpressionProcessor expressionProcessor) {
		this.sidekick = sidekick;
		this.dataTypeProcessor = dataTypeProcessor;
		this.expressionProcessor = expressionProcessor;
		this.definitions = new HashMap<>();
	}

	@NotNull
	public Map<String, Named> getDefinitions() {
		return definitions;
	}

	public void processPorts(@NotNull CmList<PortDefinitionGroup> cmPortList) {
		for (PortDefinitionGroup untypedPortDefinitionGroup : cmPortList.getAll()) {
			if (untypedPortDefinitionGroup instanceof PortDefinitionGroup_Valid) {
				PortDefinitionGroup_Valid portDefinitionGroup = (PortDefinitionGroup_Valid) untypedPortDefinitionGroup;
				for (PortDefinition portDefinition : portDefinitionGroup.getDefinitions().getAll()) {
					DataType dataType = portDefinitionGroup.getDataType();
					ProcessedDataType processedDataType = dataTypeProcessor.processDataType(dataType);
					ProcessedDataType.Family family = processedDataType.getFamily();
					if (family != ProcessedDataType.Family.UNKNOWN && family != ProcessedDataType.Family.BIT && family != ProcessedDataType.Family.VECTOR && family != ProcessedDataType.Family.CLOCK) {
						sidekick.onError(dataType, family.getDisplayString() + " type not allowed for ports");
						processedDataType = ProcessedDataType.Unknown.INSTANCE;
					}
					add(new ModulePort(portDefinition.getIdentifier(), portDefinitionGroup.getDirection(), dataType, processedDataType));
				}
			}
		}
	}

	/**
	 * Processes the specified implementation item to obtain named definitions and adds them to the definition map
	 * stored in this processor.
	 * <p>
	 * Any definition that is a constant gets its initializer processed and evaluated. Constants used in the initializer
	 * must be present in the definition map. If the current implementation item contains multiple constants, then their
	 * initializers may use constants that appear earlier in the current implementation item.
	 * <p>
	 * Any signal-like definitions that are not constants do not get their initializer processed yet -- that must be
	 * done later by the caller. This allows the current item's initializers to use definitions that only appear in
	 * later implementation items.
	 * <p>
	 * Usage note: This method must first be called for all constants in the order they appear in the module, then for
	 * all non-constants (in any order). The former ensures that each constant is available for all constants appearing
	 * later. The latter ensures that the type specifiers for non-constants can use constants defined later.
	 */
	public void process(@NotNull ImplementationItem implementationItem) {
		if (implementationItem instanceof ImplementationItem_SignalLikeDefinitionGroup) {
			ImplementationItem_SignalLikeDefinitionGroup signalLike = (ImplementationItem_SignalLikeDefinitionGroup) implementationItem;
			SignalLikeKind kind = signalLike.getKind();
			DataType dataType = signalLike.getDataType();
			ProcessedDataType processedDataType = dataTypeProcessor.processDataType(dataType);
			ProcessedDataType.Family dataTypeFamily = processedDataType.getFamily();
			for (SignalLikeDefinition signalLikeDefinition : signalLike.getDefinitions().getAll()) {

				// extract name element and initializer
				CmToken nameElement;
				ExtendedExpression initializer;
				if (signalLikeDefinition instanceof SignalLikeDefinition_WithoutInitializer) {
					SignalLikeDefinition_WithoutInitializer typedDeclaredSignalLike = (SignalLikeDefinition_WithoutInitializer) signalLikeDefinition;
					nameElement = typedDeclaredSignalLike.getIdentifier();
					initializer = null;
				} else if (signalLikeDefinition instanceof SignalLikeDefinition_WithInitializer) {
					SignalLikeDefinition_WithInitializer typedDeclaredSignalLike = (SignalLikeDefinition_WithInitializer) signalLikeDefinition;
					nameElement = typedDeclaredSignalLike.getIdentifier();
					initializer = typedDeclaredSignalLike.getInitializer();
				} else {
					sidekick.onError(signalLikeDefinition, "unknown CM node");
					continue;
				}

				// add the definition
				if (kind instanceof SignalLikeKind_Constant) {
					Constant constant = new Constant(nameElement, dataType, processedDataType, initializer);
					if (dataTypeFamily == ProcessedDataType.Family.CLOCK) {
						sidekick.onError(signalLikeDefinition, "clock type is not allowed for constants");
					} else if (initializer == null) {
						sidekick.onError(signalLikeDefinition, "constant must have an initializer");
					} else {
						constant.processExpressions(expressionProcessor);
						constant.evaluate(new ProcessedExpression.FormallyConstantEvaluationContext(sidekick));
					}
					add(constant);
				} else if (kind instanceof SignalLikeKind_Signal || kind instanceof SignalLikeKind_Register) {
					String kindString = (kind instanceof SignalLikeKind_Signal ? "signal" : "register");
					if (dataTypeFamily == ProcessedDataType.Family.MATRIX) {
						if (kind instanceof SignalLikeKind_Signal) {
							sidekick.onError(dataType, "matrix type not allowed for signal");
							processedDataType = ProcessedDataType.Unknown.INSTANCE;
						}
					} else if (dataTypeFamily == ProcessedDataType.Family.CLOCK) {
						if (kind instanceof SignalLikeKind_Register) {
							sidekick.onError(dataType, "clock type not allowed for register");
							processedDataType = ProcessedDataType.Unknown.INSTANCE;
						}
					} else if (dataTypeFamily != ProcessedDataType.Family.UNKNOWN &&
						dataTypeFamily != ProcessedDataType.Family.BIT &&
						dataTypeFamily != ProcessedDataType.Family.VECTOR) {

						sidekick.onError(dataType, dataTypeFamily.getDisplayString() + " type not allowed for " + kindString);
						processedDataType = ProcessedDataType.Unknown.INSTANCE;
					}
					if (kind instanceof SignalLikeKind_Signal) {
						add(new Signal(nameElement, dataType, processedDataType, initializer));
					} else {
						add(new Register(nameElement, dataType, processedDataType, initializer));
					}
				}

			}
		} else if (implementationItem instanceof ImplementationItem_ModuleInstanceDefinitionGroup) {
			ImplementationItem_ModuleInstanceDefinitionGroup moduleInstanceDefinitionGroupElement = (ImplementationItem_ModuleInstanceDefinitionGroup) implementationItem;

			// resolve the module definition
			Module resolvedModule;
			try {
				resolvedModule = Environment.Holder.INSTANCE.resolveModuleReference(moduleInstanceDefinitionGroupElement.getModuleName());
			} catch (ReferenceResolutionException e) {
				sidekick.onError(moduleInstanceDefinitionGroupElement.getModuleName(), e.getMessage());
				for (ModuleInstanceDefinition definition : moduleInstanceDefinitionGroupElement.getDefinitions().getAll()) {
					add(new ModuleInstanceWithMissingDefinition(moduleInstanceDefinitionGroupElement.getModuleName(), definition));
				}
				return;
			}
			ModuleApi moduleApi = new ModuleApi(resolvedModule);

			// build a map of all ports
			Map<String, InstancePort> ports = new HashMap<>();
			for (ModuleApi.Port apiPort : moduleApi.getPorts()) {
				ports.put(apiPort.getName(), new InstancePort(apiPort.getName(), apiPort.getDirection(), apiPort.getType()));
			}

			// add a module instance definition for each instance identifier
			for (ModuleInstanceDefinition definition : moduleInstanceDefinitionGroupElement.getDefinitions().getAll()) {
				add(new ModuleInstance(definition, moduleApi, ImmutableMap.copyOf(ports)));
			}

		}
	}

	private void add(@NotNull Named element) {
		if (definitions.put(element.getName(), element) != null) {
			sidekick.onError(element.getNameElement(), "redefinition of '" + element.getName() + "'");
		}
	}

}
