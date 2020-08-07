package name.martingeisse.mahdl.compiler;

import name.martingeisse.mahdl.common.ModuleIdentifier;

import java.io.IOException;
import java.io.InputStream;

public final class InputModuleEntry {

    private final ModuleIdentifier identifier;
    private final Loader loader;

    public InputModuleEntry(ModuleIdentifier identifier, Loader loader) {
        this.identifier = identifier;
        this.loader = loader;
    }

    public ModuleIdentifier getIdentifier() {
        return identifier;
    }

    public Loader getLoader() {
        return loader;
    }

    public interface Loader {
        InputStream load() throws IOException;
    }

}
