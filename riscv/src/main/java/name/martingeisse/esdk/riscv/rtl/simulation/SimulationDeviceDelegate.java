package name.martingeisse.esdk.riscv.rtl.simulation;

public interface SimulationDeviceDelegate {

    int read(int wordAddress);

    void write(int wordAddress, int byteMask, int data);

}
