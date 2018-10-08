package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 * A write transaction occurs when the ready and write-strobe signals are active at the same time. Any other
 * combination means that no write transaction occurs.
 *
 * TODO
 * handshake signals:
 * - ready (ready during the cycle when request gets asserted; ready removed on clock edge that
 *   samples the request unless immediately ready again). Should not be ready when processing a
 *   request since a result will be returned!
 *
 * BUT: ready is 1 cycle too late? --> all this is too late if the row gets closed!
 *
 * - acknowledge read: true when the result gets returned (BUT too late! need next address earlier
 *   than that for back-to-back reads!)
 * - ack write: true when address / data no longer needed
 *
 * We have to consider another read or write directly after considering the previous one
 * to allow back-to-back writes or reads.
 *
 * BETTER:
 *
 * ready / ack for commands (EN, R/W, adr, w-data)
 *
 * read data strobe: r-data; no ack (has to be ack'ed when it arrives since next r-data is
 * 	already on the way)
 *
 * Ready or ack for commands?
 */
public interface FramebufferDisplay {

	RtlBitSignal getReadySignal();
	void setWriteStrobeSignal(RtlBitSignal writeStrobeSignal);
	void setWriteAddressSignal(RtlVectorSignal writeAddressSignal);
	void setWriteDataSignal(RtlVectorSignal writeDataSignal);

}
