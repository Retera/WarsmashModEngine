package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CDamageType implements CHandle {
	UNKNOWN(false, false, false, true),
	UNKNOWN_CODE_1(false, false, false, true),
	UNKNOWN_CODE_2(false, false, false, true),
	UNKNOWN_CODE_3(false, false, false, true),
	NORMAL(false, false, true, false),
	ENHANCED(false, false, true, false),
	UNKNOWN_CODE_6(false, false, false, true),
	UNKNOWN_CODE_7(false, false, false, true),
	FIRE(false, true, false, true),
	COLD(false, true, false, true),
	LIGHTNING(false, true, false, true),
	POISON(false, false, true, true),
	DISEASE(false, false, true, true),
	DIVINE(false, true, false, true),
	MAGIC(false, true, false, true),
	SONIC(false, true, false, true),
	ACID(false, false, true, true),
	FORCE(false, true, false, true),
	DEATH(false, true, false, true),
	MIND(false, true, false, true),
	PLANT(false, true, false, true),
	DEFENSIVE(false, true, false, true),
	DEMOLITION(false, false, true, false),
	SLOW_POISON(false, false, true, true),
	SPIRIT_LINK(false, true, false, true),
	SHADOW_STRIKE(false, true, false, true),
	UNIVERSAL(true, false, false, false);

	private boolean universal;
	private boolean magic;
	private boolean physical;
	private boolean oldMagic;

	private CDamageType(boolean universal, boolean magic, boolean physical, boolean oldMagic) {
		this.universal = universal;
		this.magic = magic;
		this.physical = physical;
		this.oldMagic = oldMagic;
	}

	public static CDamageType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public boolean isUniversal() {
		return this.universal;
	}

	public boolean isMagic() {
		return this.magic;
	}

	public boolean isPhysical() {
		return this.physical;
	}

	public boolean isOldMagic() {
		return this.oldMagic;
	}
}
