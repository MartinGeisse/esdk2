package name.martingeisse.esdk.library.bus.bus32;

/**
 *
 */
public interface BusSlave {

	int getAddressBits();

	void read(int address, ReadCallback callback);

	void write(int address, int data, WriteCallback callback);

}
