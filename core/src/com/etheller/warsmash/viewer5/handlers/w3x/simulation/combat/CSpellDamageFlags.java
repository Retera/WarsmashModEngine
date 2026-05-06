package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public class CSpellDamageFlags extends CBaseDamageFlags {

	public CSpellDamageFlags() {
		super();
		this.setRanged(true);
	}
	
	private CSpellDamageFlags(CSpellDamageFlags base) {
		super(base);
	}
	
	@Override
	public CDamageFlags copy() {
		return new CSpellDamageFlags(this);
	}

	@Override
	public boolean isAttack() {
		return false;
	}

}
