/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlPin extends RtlItem {

	private String id;
	private RtlPinConfiguration configuration;

	public RtlPin(RtlDomain design) {
		super(design);

		DesignRegistrationKey key = new DesignRegistrationKey();
		design.registerPin(key, this);
		key.valid = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RtlPinConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(RtlPinConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getNetName() {
		return "pin" + id;
	}

	public abstract String getVerilogDirectionKeyword();

	public void writeModuleLocalDeclarations(VerilogWriter out) {
		// TODO
	}

	/**
	 * This class is used to ensure that
	 * {@link RtlDomain#registerClockNetwork(RtlClockNetwork.DomainRegistrationKey, RtlClockNetwork)} isn't called
	 * except through the {@link RtlClockNetwork} constructor.
	 */
	public static final class DesignRegistrationKey {

		private boolean valid = true;

		private DesignRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

}
