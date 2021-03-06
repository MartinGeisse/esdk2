/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
import name.martingeisse.mahdl.input.cm.ModuleInstanceDefinition;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class ModuleInstance extends Named {

    @NotNull
    private final ModuleInstanceDefinition moduleInstanceDefinitionElement;

    @NotNull
    private final ModuleApi moduleApi;

    @NotNull
    private final ImmutableMap<String, InstancePort> ports;

    public ModuleInstance(@NotNull ModuleInstanceDefinition moduleInstanceDefinitionElement,
                          @NotNull ModuleApi moduleApi,
                          @NotNull ImmutableMap<String, InstancePort> ports) {
        super(moduleInstanceDefinitionElement.getIdentifier());
        this.moduleInstanceDefinitionElement = moduleInstanceDefinitionElement;
        this.moduleApi = moduleApi;
        this.ports = ports;
    }

    @NotNull
    public ModuleInstanceDefinition getModuleInstanceDefinitionElement() {
        return moduleInstanceDefinitionElement;
    }

    @NotNull
    public ModuleApi getModuleApi() {
        return moduleApi;
    }

    @NotNull
    public ImmutableMap<String, InstancePort> getPorts() {
        return ports;
    }

    @Override
    public void processExpressions(@NotNull ExpressionProcessor expressionProcessor) {
    }

}
