package name.martingeisse.esdk.library.mybus.transaction;

/**
 *
 */
public interface TransactionMybusSlave {

	int getLocalAddressBits();

	int read(int localAddress);

	void write(int localAddress, int data, int byteMask);

}
