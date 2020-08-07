package name.martingeisse.mahdl.compiler;

import com.google.common.collect.ImmutableCollection;
import name.martingeisse.mahdl.common.ModuleApi;
import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.input.cm.CmLinked;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Any environment calling the compiler must provide this context.
 */
public interface CompilerContext {

    //region access to input files

    ImmutableCollection<InputModuleEntry> getInputModuleEntries();

    //endregion
    //region dependency resolution

    /**
     * Returns null if not found. This is not an error case since multiple loaders may be combined, and only one of
     * them should find the metadata for a specific module.
     */
    ModuleApi readDependencyModuleApi(ModuleIdentifier identifier) throws IOException;

    InputStream openDataInputFile(ModuleIdentifier anchorIdentifier, String filename) throws IOException;

    //endregion
    //region output generation

    void writeOutputModuleApi(ModuleIdentifier identifier, ModuleApi moduleApi) throws IOException;

    OutputStream openCodeOutputFile(ModuleIdentifier identifier) throws IOException;

    OutputStream openDataOutputFile(ModuleIdentifier anchorIdentifier, String filename) throws IOException;

    //endregion
    //region error reporting

    void reportDiagnostic(CmLinked errorSource, String message, Throwable exception);

    default void reportDiagnostic(CmLinked errorSource, String message) {
        reportDiagnostic(errorSource, message, null);
    }

    void reportError(CmLinked errorSource, String message, Throwable exception);

    default void reportError(CmLinked errorSource, String message) {
        reportError(errorSource, message, null);
    }

    default void reportDiagnostic(ModuleIdentifier moduleIdentifier, int row, int column, String message) {
        reportDiagnostic(moduleIdentifier, row, column, message, null);
    }

    void reportDiagnostic(ModuleIdentifier moduleIdentifier, int row, int column, String message, Throwable exception);

    default void reportError(ModuleIdentifier moduleIdentifier, int row, int column, String message) {
        reportError(moduleIdentifier, row, column, message, null);
    }

    void reportError(ModuleIdentifier moduleIdentifier, int row, int column, String message, Throwable exception);

    //endregion

    static CompilerContext get() {
        return CompilerContextHolder.get();
    }

}
