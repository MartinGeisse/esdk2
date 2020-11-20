package name.martingeisse.esdk.riscv.gamesys;

import org.apache.commons.io.IOUtils;

import java.io.File;

public class GamesysCompilerInvoker {

    public static void invoke() throws Exception {
        invoke("riscv/resource/gamesys");
    }

    public static void invoke(String path) throws Exception {
        File directory = new File(path);
        File buildScript = new File(directory, "build.php");
        Process process = Runtime.getRuntime().exec(buildScript.getAbsolutePath(), null, directory);
        int status = process.waitFor();
        if (status != 0 || !new File(directory, "build/program.bin").exists()) {
            System.err.println();
            System.err.println("***************************************");
            System.err.println("*** ERROR WHILE COMPILING C PROGRAM ***");
            System.err.println("***************************************");
            System.err.println();
            System.err.println("path: " + path);
            System.err.println();
            IOUtils.copy(process.getInputStream(), System.err);
            IOUtils.copy(process.getErrorStream(), System.err);
            System.err.println("C build script status code " + status);
            System.err.flush();
            System.exit(1);
        }
    }

}
