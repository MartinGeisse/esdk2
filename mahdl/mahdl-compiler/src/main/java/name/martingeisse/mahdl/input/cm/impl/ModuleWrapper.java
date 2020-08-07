package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.compiler.ModuleCompiler;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.Module;

/**
 *
 */
public final class ModuleWrapper extends CmNodeImpl {

    private final ModuleIdentifier identifier;
    private final Module module;
    private final ModuleCompiler compiler;

    public ModuleWrapper(ModuleIdentifier identifier, Module module, ModuleCompiler compiler) {
        super(0, 0);
        this.identifier = identifier;
        this.module = module;
        ((CmNodeImpl) this.module).setParent(this);
        this.compiler = compiler;
    }

    public ModuleIdentifier getIdentifier() {
        return identifier;
    }

    public Module getModule() {
        return module;
    }

    public ModuleCompiler getCompiler() {
        return compiler;
    }

    public static ModuleWrapper get(CmNode node) {
        while (node.getCmParent() != null) {
            node = node.getCmParent();
        }
        return (node instanceof ModuleWrapper) ? (ModuleWrapper) node : null;
    }

}
