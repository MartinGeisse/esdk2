package name.martingeisse.esdk.riscv.tools;

import name.martingeisse.esdk.riscv.rtl.CeeCompilerInvoker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class CopyProgramToBootClientMain {

    public static void main(String[] args) throws Exception {
        CeeCompilerInvoker.invoke();
        System.out.println("starting to send...");
        File file = new File("/home/martin/git-repos/esdk2/riscv/resource/gfx-program/build/program.bin");
        File device = findDeviceFile();
        try (FileInputStream in = new FileInputStream(file)) {
            try (FileOutputStream out = new FileOutputStream(device)) {
                while (true) {
                    int x = in.read();
                    if (x < 0) {
                        break;
                    }
                    out.write(x);
                    // The boot client cannot receive from us and send to the FPGA at the same time since the latter
                    // is a software-based serial port implementation that has to disable interrupts for precise
                    // timing. So we have to put in a small delay here to ensure the previous byte has been sent to the
                    // FPGA before sending the next one.
                    Thread.sleep(0, 1_000);
                }
            }
        }
        System.out.println("finished to send");
    }

    private static File findDeviceFile() {
        File folder = new File("/dev");
        File[] files = folder.listFiles((dir, name) -> name.startsWith("ttyACM"));
        if (files.length != 1) {
            throw new RuntimeException("could not detect device file: " + Arrays.asList(files));
        }
        return files[0];
    }

}
