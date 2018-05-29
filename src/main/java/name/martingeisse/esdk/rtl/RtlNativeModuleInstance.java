/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import com.google.common.collect.ImmutableMap;
import name.martingeisse.esdk.rtl.signal.RtlSignal;

/**
 *
 */
public abstract class RtlNativeModuleInstance {

	// TODO unklar, ob das so passt
	protected abstract ImmutableMap<String, Object> getEffectiveModuleParameters();
	protected abstract ImmutableMap<String, RtlSignal> getEffectiveInputSignals();
	protected abstract ImmutableMap<String, RtlSignal> getEffectiveOutputSignals();

}
