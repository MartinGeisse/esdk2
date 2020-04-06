package name.martingeisse.esdk.riscv.rtl;

import org.apache.commons.io.IOUtils;

import java.io.File;

public class CeeCompilerInvoker {

    public static void invoke() throws Exception {
        File directory = new File("riscv/resource/gfx-program");
        File buildScript = new File(directory, "build.php");
        Process process = Runtime.getRuntime().exec(buildScript.getAbsolutePath(), null, directory);
        int status = process.waitFor();
        if (status != 0 || !new File(directory, "build/program.bin").exists()) {
            System.err.println();
            System.err.println("***************************************");
            System.err.println("*** ERROR WHILE COMPILING C PROGRAM ***");
            System.err.println("***************************************");
            System.err.println();
            IOUtils.copy(process.getInputStream(), System.err);
            IOUtils.copy(process.getErrorStream(), System.err);
            System.err.println("C build script status code " + status);
            System.err.flush();
            System.exit(0);
        }
    }

}
