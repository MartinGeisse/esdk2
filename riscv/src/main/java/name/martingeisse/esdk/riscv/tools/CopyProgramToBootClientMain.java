package name.martingeisse.esdk.riscv.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CopyProgramToBootClientMain {

    public static void main(String[] args) throws Exception {
        System.out.println("starting to send...");
        File file = new File("/home/martin/git-repos/esdk2/riscv/resource/gfx-program/build/program.bin");
        File device = new File("/dev/ttyACM0");
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
                    Thread.sleep(0, 20_000);
                }
            }
        }
        System.out.println("finished to send");
    }
}
