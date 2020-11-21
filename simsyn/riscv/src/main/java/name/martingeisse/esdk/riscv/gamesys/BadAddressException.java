package name.martingeisse.esdk.riscv.gamesys;

public class BadAddressException extends RuntimeException {

    private String action;
    private int wordAddress;
    private int data;
    private int byteMask;

    public BadAddressException() {
    }

    private BadAddressException(String message, String action, int wordAddress, int data, int byteMask) {
        super(message);
        this.action = action;
        this.wordAddress = wordAddress;
        this.data = data;
        this.byteMask = byteMask;
    }

    public static BadAddressException forInstructionFetch(int wordAddress) {
        return new BadAddressException("unexpected instruction fetch at word address " + wordAddress + " (" + Integer.toHexString(wordAddress),
                "instruction fetch", wordAddress, 0, 0);
    }

    public static BadAddressException forRead(int wordAddress) {
        return new BadAddressException("unexpected read at word address " + wordAddress + " (" + Integer.toHexString(wordAddress),
                "read", wordAddress, 0, 0);
    }

    public static BadAddressException forWrite(int wordAddress, int data, int byteMask) {
        return new BadAddressException("unexpected write at word address " + wordAddress + " (" + Integer.toHexString(wordAddress) +
                " with data " + data + " (" + Integer.toHexString(data) + ", write mask " + Integer.toString(byteMask, 2),
                "write", wordAddress, data, byteMask);
    }

    public String getAction() {
        return action;
    }

    public int getWordAddress() {
        return wordAddress;
    }

    public int getData() {
        return data;
    }

    public int getByteMask() {
        return byteMask;
    }

}
