/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlPin extends RtlItem {

	private String id;
	private RtlPinConfiguration configuration;

	public RtlPin(RtlRealm realm) {
		super(realm);

		RealmRegistrationKey key = new RealmRegistrationKey();
		realm.registerPin(key, this);
		key.valid = false;
	}

	/**
	 * This class is used to ensure that
	 * {@link RtlRealm#registerClockNetwork(RtlClockNetwork.RealmRegistrationKey, RtlClockNetwork)} isn't called
	 * except through the {@link RtlClockNetwork} constructor.
	 */
	public static final class RealmRegistrationKey {

		private boolean valid = true;

		private RealmRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

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

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	public abstract String getVerilogDirectionKeyword();

	public void writeModuleLocalDeclarations(VerilogWriter out) {
		// TODO
	}

}
