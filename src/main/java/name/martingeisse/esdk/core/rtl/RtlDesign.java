/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.block.RtlAsynchronousBlock;
import name.martingeisse.esdk.core.rtl.block.RtlBlock;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: asynchronous blocks cache their results implicitly, inside their RtlProceduralSignal objects.
 * They have to be updated immediately when their inputs change (Verilog: always @(*) does that).
 * But we can't right now. Solutions:
 * - allow to register listeners to RtlSignal. Cumbersome for custom signal writers and we have to
 * implement some kind of "grouping" to avoid registering lots of event callbacks
 * - don't allow custom RtlSignal implementations. Other models must set the value explicitly, and this
 * calls listeners / block execution.
 * - additionally allow custom RtlSignal implementations that must support listeners. No advantages over
 * calling a setter -- code is still as complex and it's even more error-prone.
 */
public final class RtlDesign extends Item {

	private final List<RtlPin> pins = new ArrayList<>();
	private final List<RtlClockNetwork> clockNetworks = new ArrayList<>();
	private final List<RtlBlock> blocks = new ArrayList<>();

	public RtlDesign(Design design) {
		super(design);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerPin(RtlPin.DesignRegistrationKey key, RtlPin pin) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		pins.add(pin);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerClockNetwork(RtlClockNetwork.DesignRegistrationKey key, RtlClockNetwork clockNetwork) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		clockNetworks.add(clockNetwork);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerBlock(RtlBlock.DesignRegistrationKey key, RtlBlock block) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		blocks.add(block);
	}

	public Iterable<RtlPin> getPins() {
		return pins;
	}

	public Iterable<RtlClockNetwork> getClockNetworks() {
		return clockNetworks;
	}

	public Iterable<RtlBlock> getBlocks() {
		return blocks;
	}

	public RtlClockNetwork createClockNetwork(RtlBitSignal clockSignal) {
		return new RtlClockNetwork(this, clockSignal);
	}

	public RtlAsynchronousBlock createAsynchronousBlock() {
		return new RtlAsynchronousBlock(this);
	}

	@Override
	protected void initializeSimulation() {
		if (!pins.isEmpty()) {
			throw new IllegalStateException("cannot use an RtlDesign with pins in simulation");
		}
	}

	public void fireClockEdge(RtlClockNetwork clockNetwork) {
		fire(() -> onClockEdge(clockNetwork), 0);
	}

	private void onClockEdge(RtlClockNetwork clockNetwork) {
		for (RtlBlock block : blocks) {
			if (block instanceof RtlClockedBlock) {
				RtlClockedBlock clockedBlock = (RtlClockedBlock) block;
				if (clockedBlock.getClockNetwork() == clockNetwork) {
					clockedBlock.execute();
				}
			}
		}
		List<RtlProceduralSignal> changedSignals = new ArrayList<>();
		for (RtlBlock block : blocks) {
			if (block instanceof RtlClockedBlock) {
				RtlClockedBlock clockedBlock = (RtlClockedBlock) block;
				if (clockedBlock.getClockNetwork() == clockNetwork) {
					clockedBlock.updateProceduralSignals(changedSignals);
				}
			}
		}
		// TODO run triggered asynchronous blocks
	}

	// TODO: A changed signal may result in execution of an asynchronous block, but one of the
	// intermediate signal "expression" objects may be shared with another block. Since it's an
	// intermediate object, that other block won't be notified!
	//
	// solution 1: That other block must have the originally changed signal in its trigger list
	// 		then each signal must support getting its source signals --> error-prone. If you
	//      forget one, bugs happen.
	//		On the other hand, the set of signal classes is meant to be bounded anyway.
	//		Also, the trigger list must include all "direct" triggers but must not include any
	//		"indirect" triggers -- signals that when changed will eventually cause a change, but
	//		will only do so "later" through a delayed event. So updating the listening signal
	//		immediately will get the wrong value.
	// solution 2: any intermediate signal knows its current value and supports listeners
	// 		may be slow, but supports caching out of the box and will only trigger subsequent blocks
	//		if a signal actually changes. This replaces "pull" signal evaluation by "push" signal
	//		updating, like Verilog does.
	//		Maybe this also allows custom RtlSignal implementations, which can be used to speed
	//		up simulation.
	public void notifySignalChanged() {

	}

}
