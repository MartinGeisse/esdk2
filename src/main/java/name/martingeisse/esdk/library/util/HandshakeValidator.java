package name.martingeisse.esdk.library.util;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a pure simulation item that validates the control and data signals for a simple handshake protocol:
 * <p>
 * - The producer-side asserts a request signal and data signals. These signals are kept stable over one or several
 * clock edges.
 * <p>
 * - The consumer-side performs the request and when done, responds with a pre-acknowledge and/or post-acknowledge
 * signal. The pre-acknowledge signal is 1 during the clock cycle just before the last clock edge in which the
 * data signals must be kept stable; the post-acknowledge signal is 1 during the clock cycle just after that edge.
 * If both signals are provided, they must be consistent.
 * <p>
 * - This validator is configurable with respect to whether the request signal must be kept high while the request
 * is being performed, or must be high only for the first clock edge (that signals the request), or gets ignored
 * after the first edge.
 * <p>
 * - Optionally, the consumer can provide a ready signal that gets asserted after the clock edge that finishes a
 * request (just like the post-acknowledge) but stays high afterwards. Note that the interface may still signal "ready"
 * when it is in fact not accepting requests "right now" -- the signal just indicates that request data may change from
 * the previous clock cycle, and does not indicate expected response times. The ready signal must not change to low
 * without a request being accepted, as this creates confusion for the producer: It signals to keep request data
 * stable when no request has been made. Both properties of the ready signal taken together that the ready signal
 * should stay high when the consumer is busy with "other stuff".
 * <p>
 * - The ready signal may initially be low to simplify initialization, but once it turns high, should stay high until
 * a request gets processed.
 * <p>
 * - At least one of the three confirmation signals (pre-acknowledge, post-acknowledge, ready) must be provided so this
 * validator knows when the request has been finished.
 *
 * <p>
 * This item vanishes during synthesis.
 */
public class HandshakeValidator extends RtlClockedItem {

	private RtlBitSignal readySignal;
	private RtlBitSignal requestSignal;
	private RtlBitSignal preAcknowledgeSignal;
	private RtlBitSignal postAcknowledgeSignal;
	private List<RtlSignal> requestDataSignals = new ArrayList<>();

	boolean initializationInProgress;
	boolean sampledReadyValue;
	boolean sampledRequestValue;
	boolean sampledPreAcknowledgeValue;
	boolean sampledPostAcknowledgeValue;
	private RtlBitSignal[] cachedBitDataSignals;
	private boolean[] sampledBitDataValues;
	private RtlVectorSignal[] cachedVectorDataSignals;
	private VectorValue[] sampledVectorDataValues;

	public HandshakeValidator(RtlClockNetwork clockNetwork) {
		super(clockNetwork);
	}

	public RtlBitSignal getReadySignal() {
		return readySignal;
	}

	public void setReadySignal(RtlBitSignal readySignal) {
		this.readySignal = readySignal;
	}

	public RtlBitSignal getRequestSignal() {
		return requestSignal;
	}

	public void setRequestSignal(RtlBitSignal requestSignal) {
		this.requestSignal = requestSignal;
	}

	public RtlBitSignal getPreAcknowledgeSignal() {
		return preAcknowledgeSignal;
	}

	public void setPreAcknowledgeSignal(RtlBitSignal preAcknowledgeSignal) {
		this.preAcknowledgeSignal = preAcknowledgeSignal;
	}

	public RtlBitSignal getPostAcknowledgeSignal() {
		return postAcknowledgeSignal;
	}

	public void setPostAcknowledgeSignal(RtlBitSignal postAcknowledgeSignal) {
		this.postAcknowledgeSignal = postAcknowledgeSignal;
	}

	public List<RtlSignal> getRequestDataSignals() {
		return requestDataSignals;
	}

	public void setRequestDataSignals(List<RtlSignal> requestDataSignals) {
		this.requestDataSignals = requestDataSignals;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		if (requestSignal == null) {
			throw new IllegalStateException("no request signal");
		}
		if (preAcknowledgeSignal == null && postAcknowledgeSignal == null && readySignal == null) {
			throw new IllegalStateException("no confirmation signals");
		}
		initializationInProgress = true;
		sampledReadyValue = false;
		sampledRequestValue = false;
		sampledPreAcknowledgeValue = false;
		sampledPostAcknowledgeValue = false;
		int bitSignalCount = 0, vectorSignalCount = 0;
		for (RtlSignal signal : requestDataSignals) {
			if (signal instanceof RtlBitSignal) {
				bitSignalCount++;
			} else if (signal instanceof RtlVectorSignal) {
				vectorSignalCount++;
			} else {
				throw new IllegalStateException("invalid data signal for validation: " + signal);
			}
		}
		cachedBitDataSignals = new RtlBitSignal[bitSignalCount];
		sampledBitDataValues = new boolean[bitSignalCount];
		cachedVectorDataSignals = new RtlVectorSignal[vectorSignalCount];
		sampledVectorDataValues = new VectorValue[vectorSignalCount];
		bitSignalCount = 0;
		vectorSignalCount = 0;
		for (RtlSignal signal : requestDataSignals) {
			if (signal instanceof RtlBitSignal) {
				cachedBitDataSignals[bitSignalCount] = (RtlBitSignal) signal;
				bitSignalCount++;
			} else {
				RtlVectorSignal vectorSignal = (RtlVectorSignal) signal;
				cachedVectorDataSignals[vectorSignalCount] = vectorSignal;
				sampledVectorDataValues[vectorSignalCount] = VectorValue.ofUnsigned(vectorSignal.getWidth(), 0);
				vectorSignalCount++;
			}
		}
	}

	@Override
	public void computeNextState() {

		// analyze and validate control signals
		boolean expectStableData = false;
		if (!initializationInProgress) {
			/*
			TODO
			boolean sampledReadyValue;
			boolean sampledRequestValue;
			boolean sampledPreAcknowledgeValue;
			boolean sampledPostAcknowledgeValue;
			*/
		}

		// if we expect the data signals to be held stable, validate that
		if (expectStableData) {
			for (int i = 0; i < cachedBitDataSignals.length; i++) {
				if (cachedBitDataSignals[i].getValue() != sampledBitDataValues[i]) {
					onError("data not stable during request: " + cachedBitDataSignals[i] + " changed from " +
						sampledBitDataValues[i] + " to " + cachedBitDataSignals[i].getValue());
				}
			}
			for (int i = 0; i < cachedVectorDataSignals.length; i++) {
				if (!cachedVectorDataSignals[i].getValue().equals(sampledVectorDataValues[i])) {
					onError("data not stable during request: " + cachedVectorDataSignals[i] + " changed from " +
						sampledVectorDataValues[i] + " to " + cachedVectorDataSignals[i].getValue());
				}
			}
		}

		// store new sampled values
		initializationInProgress = false;
		sampledReadyValue = readySignal.getValue();
		sampledRequestValue = requestSignal.getValue();
		sampledPreAcknowledgeValue = preAcknowledgeSignal.getValue();
		sampledPostAcknowledgeValue = postAcknowledgeSignal.getValue();
		for (int i = 0; i < cachedBitDataSignals.length; i++) {
			sampledBitDataValues[i] = cachedBitDataSignals[i].getValue();
		}
		for (int i = 0; i < cachedVectorDataSignals.length; i++) {
			sampledVectorDataValues[i] = cachedVectorDataSignals[i].getValue();
		}

	}

	protected void onError(String detailMessage) {
		throw new RuntimeException("handshake validation error: " + detailMessage);
	}

	@Override
	public void updateState() {
		// no state updates needed since we have no outputs
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
