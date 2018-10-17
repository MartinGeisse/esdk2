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
 * <p>
 * <p>
 * This item vanishes during synthesis.
 */
public class HandshakeValidator extends RtlClockedItem {

	private RtlBitSignal readySignal;
	private RtlBitSignal requestSignal;
	private RequestSignalContinuationExpectation requestSignalContinuationExpectation;
	private RtlBitSignal preAcknowledgeSignal;
	private RtlBitSignal postAcknowledgeSignal;
	private List<RtlSignal> requestDataSignals = new ArrayList<>();

	private RtlBitSignal[] cachedBitDataSignals;
	private RtlVectorSignal[] cachedVectorDataSignals;

	boolean sampledReadyValue;
	boolean sampledRequestValue;
	boolean sampledPreAcknowledgeValue;
	boolean sampledPostAcknowledgeValue;
	private boolean[] sampledBitDataValues;
	private VectorValue[] sampledVectorDataValues;

	boolean previousReadyValue;
	boolean previousRequestValue;
	boolean previousPreAcknowledgeValue;
	boolean previousPostAcknowledgeValue;
	private boolean[] previousBitDataValues;
	private VectorValue[] previousVectorDataValues;

	private RequestState requestState;

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

	public RequestSignalContinuationExpectation getRequestSignalContinuationExpectation() {
		return requestSignalContinuationExpectation;
	}

	public void setRequestSignalContinuationExpectation(RequestSignalContinuationExpectation requestSignalContinuationExpectation) {
		this.requestSignalContinuationExpectation = requestSignalContinuationExpectation;
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

	public enum RequestSignalContinuationExpectation {
		TRUE,
		FALSE,
		DONT_CARE;

		public boolean isValid(boolean continuationValue) {
			return (this == TRUE && continuationValue) || (this == FALSE && !continuationValue) || (this == DONT_CARE);
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		if (requestSignal == null) {
			throw new IllegalStateException("no request signal");
		}
		if (requestSignalContinuationExpectation == null) {
			throw new IllegalStateException("request signal continuation expectation not set");
		}
		if (preAcknowledgeSignal == null && postAcknowledgeSignal == null && readySignal == null) {
			throw new IllegalStateException("no confirmation signals");
		}

		sampledReadyValue = false;
		sampledRequestValue = false;
		sampledPreAcknowledgeValue = false;
		sampledPostAcknowledgeValue = false;

		previousReadyValue = false;
		previousRequestValue = false;
		previousPreAcknowledgeValue = false;
		previousPostAcknowledgeValue = false;

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
		previousBitDataValues = new boolean[bitSignalCount];
		cachedVectorDataSignals = new RtlVectorSignal[vectorSignalCount];
		sampledVectorDataValues = new VectorValue[vectorSignalCount];
		previousVectorDataValues = new VectorValue[vectorSignalCount];
		bitSignalCount = 0;
		vectorSignalCount = 0;
		for (RtlSignal signal : requestDataSignals) {
			if (signal instanceof RtlBitSignal) {
				cachedBitDataSignals[bitSignalCount] = (RtlBitSignal) signal;
				bitSignalCount++;
			} else {
				RtlVectorSignal vectorSignal = (RtlVectorSignal) signal;
				cachedVectorDataSignals[vectorSignalCount] = vectorSignal;
				previousVectorDataValues[vectorSignalCount] = sampledVectorDataValues[vectorSignalCount] = VectorValue.ofUnsigned(vectorSignal.getWidth(), 0);
				vectorSignalCount++;
			}
		}

		requestState = RequestState.INITIAL;
	}

	@Override
	public void computeNextState() {

		// sample all signals
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

	@Override
	public void updateState() {

		// analyze and validate control signals
		requestState.validate(this);

		// if we expect the data signals to be held stable, validate that
		if (requestState.isStableDataExpected(this)) {
			for (int i = 0; i < cachedBitDataSignals.length; i++) {
				if (sampledBitDataValues[i] != previousBitDataValues[i]) {
					onError("data not stable during request: " + cachedBitDataSignals[i] + " changed from " +
						previousBitDataValues[i] + " to " + sampledBitDataValues[i]);
				}
			}
			for (int i = 0; i < cachedVectorDataSignals.length; i++) {
				if (!sampledVectorDataValues[i].equals(previousVectorDataValues[i])) {
					onError("data not stable during request: " + cachedVectorDataSignals[i] + " changed from " +
						previousVectorDataValues[i] + " to " + sampledVectorDataValues[i]);
				}
			}
		}

		// advance request validation state -- this must happen while the current and next signal values are
		// still present, i.e. before storing the new sampled values below
		requestState = requestState.getNextState(this);

		// store sampled signals
		previousReadyValue = sampledReadyValue;
		previousRequestValue = sampledRequestValue;
		previousPreAcknowledgeValue = sampledPreAcknowledgeValue;
		previousPostAcknowledgeValue = sampledPostAcknowledgeValue;
		System.arraycopy(sampledBitDataValues, 0, previousBitDataValues, 0, sampledBitDataValues.length);
		System.arraycopy(sampledVectorDataValues, 0, previousVectorDataValues, 0, sampledVectorDataValues.length);

	}

	protected void onError(String detailMessage) {
		throw new RuntimeException("handshake validation error: " + detailMessage);
	}

	/**
	 * We keep an internal request state to interpret the control signals. During each cycle, the current request
	 * state is the one that validates the signals sampled during that cycle. Previous values are available for
	 * convenience.
	 *
	 * Also, general rules are validated outside the state object. TODO are there any??
	 * TODO how are the different keep-request-HIGH modes handled?
	 */
	private enum RequestState {
			/*
			TODO
			boolean sampledReadyValue;
			boolean sampledRequestValue;
			boolean sampledPreAcknowledgeValue;
			boolean sampledPostAcknowledgeValue;
			*/

		// like IDLE but with the ready signal kept false until now, which is initially allowed
		INITIAL {

			@Override
			void validate(HandshakeValidator validator) {
				if (validator.readySignal != null && validator.requestSignal != null &&
					!validator.sampledReadyValue && validator.sampledRequestValue) {
					validator.onError("making request while initially not ready");
				}
				if (validator.postAcknowledgeSignal != null && validator.sampledPostAcknowledgeValue) {
					validator.onError("post-acknowledge is true in the initial cycle");
				}
				if (validator.requestSignal != null && validator.preAcknowledgeSignal != null &&
					!validator.sampledRequestValue && validator.sampledPreAcknowledgeValue) {
					validator.onError("getting pre-ack without request");
				}
			}

			@Override
			RequestState getNextState(HandshakeValidator validator) {
				if (validator.sampledRequestValue) {
					if (validator.preAcknowledgeSignal != null && validator.sampledPreAcknowledgeValue) {
						return PRE_ACKNOWLEDGED;
					} else {
						return BUSY;
					}
				} else if (validator.readySignal != null && validator.sampledReadyValue) {
					return IDLE;
				} else {
					return this;
				}
			}

		},

		// no request was signalled or active in previous cycle
		IDLE {

			@Override
			void validate(HandshakeValidator validator) {
				// TODO check
//				if (validator.readySignal != null && validator.requestSignal != null &&
//					!validator.sampledReadyValue && validator.sampledRequestValue) {
//					validator.onError("making request while initially not ready");
//				}
//				if (validator.postAcknowledgeSignal != null && validator.sampledPostAcknowledgeValue) {
//					validator.onError("post-acknowledge is true in the initial cycle");
//				}
//				if (validator.requestSignal != null && validator.preAcknowledgeSignal != null &&
//					!validator.sampledRequestValue && validator.sampledPreAcknowledgeValue) {
//					validator.onError("getting pre-ack without request");
//				}
			}

			@Override
			RequestState getNextState(HandshakeValidator validator) {
				if (!validator.sampledRequestValue) {
					return this;
				} else if (validator.preAcknowledgeSignal != null && validator.sampledPreAcknowledgeValue) {
					return PRE_ACKNOWLEDGED;
				} else {
					return BUSY;
				}
			}

		},

		// request was signalled and not yet pre-acked
		BUSY {

			@Override
			void validate(HandshakeValidator validator) {
				// TODO
			}

			@Override
			boolean isStableDataExpected(HandshakeValidator validator) {
				if (validator.postAcknowledgeSignal != null && validator.sampledPostAcknowledgeValue) {
					return false;
				}
				if (validator.readySignal != null && validator.sampledReadyValue) {
					return false;
				}
				return true;
			}

			@Override
			RequestState getNextState(HandshakeValidator validator) {
				// TODO
			}

		},

		// request was pre-acked in the previous cycle
		PRE_ACKNOWLEDGED {

			@Override
			void validate(HandshakeValidator validator) {
				// TODO
			}

			@Override
			RequestState getNextState(HandshakeValidator validator) {
				return IDLE.getNextState(validator);
			}

		};

		void validate(HandshakeValidator validator) {
		}

		boolean isStableDataExpected(HandshakeValidator validator) {
			return false;
		}

		RequestState getNextState(HandshakeValidator validator) {
			return this;
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
