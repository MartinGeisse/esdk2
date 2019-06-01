package name.martingeisse.esdk.riscv.rtl.ram;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

public interface RamControllerAdapter extends RtlItemOwned {

    RtlBitSignalConnector getAcknowledge();

    void setEnable(RtlBitSignal enable);

    RtlBitSignal getEnable();

    RtlVectorSignalConnector getReadData();

    void setWordAddress(RtlVectorSignal wordAddress);

    RtlVectorSignal getWordAddress();

    void setWrite(RtlBitSignal write);

    RtlBitSignal getWrite();

    void setWriteData(RtlVectorSignal writeData);

    RtlVectorSignal getWriteData();

    void setWriteMask(RtlVectorSignal writeMask);

    RtlVectorSignal getWriteMask();

    class Implementation extends RtlItem implements RamControllerAdapter {

        private final RtlClockNetwork _clk;
        private final RtlBitSignalConnector _enable;
        private final RtlBitSignalConnector _write;
        private final RtlVectorSignalConnector _wordAddress;
        private final RtlVectorSignalConnector _writeData;
        private final RtlVectorSignalConnector _writeMask;
        private final RtlVectorSignalConnector _readData;
        private final RtlBitSignalConnector _acknowledge;

        public Implementation(RtlRealm realm, RtlClockNetwork clk) {
            super(realm);
            this._clk = clk;
            _enable = new RtlBitSignalConnector(realm);
            _write = new RtlBitSignalConnector(realm);
            _wordAddress = new RtlVectorSignalConnector(realm, 24);
            _writeData = new RtlVectorSignalConnector(realm, 32);
            _writeMask = new RtlVectorSignalConnector(realm, 4);
            _readData = new RtlVectorSignalConnector(realm, 32);
            _acknowledge = new RtlBitSignalConnector(realm);
        }

        @Override
        public VerilogContribution getVerilogContribution() {
            return new EmptyVerilogContribution();
        }

        public RtlClockNetwork get_clk() {
            return _clk;
        }

        public RtlBitSignalConnector getAcknowledge() {
            return _acknowledge;
        }

        public void setEnable(RtlBitSignal enable) {
            this._enable.setConnected(enable);
        }

        public RtlBitSignal getEnable() {
            return _enable.getConnected();
        }

        public RtlVectorSignalConnector getReadData() {
            return _readData;
        }

        public void setWordAddress(RtlVectorSignal wordAddress) {
            this._wordAddress.setConnected(wordAddress);
        }

        public RtlVectorSignal getWordAddress() {
            return _wordAddress.getConnected();
        }

        public void setWrite(RtlBitSignal write) {
            this._write.setConnected(write);
        }

        public RtlBitSignal getWrite() {
            return _write.getConnected();
        }

        public void setWriteData(RtlVectorSignal writeData) {
            this._writeData.setConnected(writeData);
        }

        public RtlVectorSignal getWriteData() {
            return _writeData.getConnected();
        }

        public void setWriteMask(RtlVectorSignal writeMask) {
            this._writeMask.setConnected(writeMask);
        }

        public RtlVectorSignal getWriteMask() {
            return _writeMask.getConnected();
        }

    }

}
