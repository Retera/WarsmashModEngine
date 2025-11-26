package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public class CSpellDamageFlags extends CBaseDamageFlags {

	public CSpellDamageFlags() {
		super();
		this.setRanged(true);
	}

	@Override
	public boolean isAttack() {
		return false;
	}

}
