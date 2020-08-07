package name.martingeisse.mahdl.gradle;

import name.martingeisse.mahdl.common.ModuleIdentifier;
import name.martingeisse.mahdl.compiler.CompilerContext;
import name.martingeisse.mahdl.input.cm.CmLinked;

public class TempCompilerContext implements CompilerContext {

    @Override
    public void reportDiagnostic(CmLinked errorSource, String message, Throwable exception) {
        CompilationErrors.reportDiagnostic(errorSource, message, exception);
    }

    @Override
    public void reportError(CmLinked errorSource, String message, Throwable exception) {
        CompilationErrors.reportError(errorSource, message, exception);
    }

    @Override
    public void reportDiagnostic(ModuleIdentifier moduleIdentifier, int row, int column, String message, Throwable exception) {
        CompilationErrors.reportDiagnostic(moduleIdentifier.toString(), row, column, message, exception);
    }

    @Override
    public void reportError(ModuleIdentifier moduleIdentifier, int row, int column, String message, Throwable exception) {
        CompilationErrors.reportError(moduleIdentifier.toString(), row, column, message, exception);
    }

}
