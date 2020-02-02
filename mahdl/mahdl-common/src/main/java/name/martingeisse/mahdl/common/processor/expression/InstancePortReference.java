/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.definition.InstancePort;
import name.martingeisse.mahdl.common.processor.definition.ModuleInstance;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class InstancePortReference extends ProcessedExpression implements Comparable<InstancePortReference> {

	@NotNull
	private final ModuleInstance moduleInstance;

	@NotNull
	private final InstancePort port;

	public InstancePortReference(@NotNull CmNode errorSource,
								 @NotNull ModuleInstance moduleInstance,
								 @NotNull InstancePort port) {
		super(errorSource, port.getDataType());
		this.moduleInstance = moduleInstance;
		this.port = port;
	}

	@NotNull
	public ModuleInstance getModuleInstance() {
		return moduleInstance;
	}

	@NotNull
	public InstancePort getPort() {
		return port;
	}

	@Override
	@NotNull
	protected ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		return context.notConstant(this);
	}

	@NotNull
	@Override
	protected ProcessedExpression performFolding(@NotNull ProcessingSidekick sidekick) {
		return this;
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		return this;
	}

	/**
	 * This is a kind of equality, but the argument is typed and we don't want to use equals() here since all other
	 * processed objects have an equals method but it won't work correctly, so that would be error-prone.
	 */
	public boolean isSameAs(InstancePortReference other) {
		return moduleInstance == other.getModuleInstance() && port == other.getPort();
	}

	@Override
	public int compareTo(@NotNull InstancePortReference o) {
		return new CompareToBuilder().append(moduleInstance.getName(), o.getModuleInstance().getName())
			.append(port.getName(), o.getPort().getName()).toComparison();
	}

}
