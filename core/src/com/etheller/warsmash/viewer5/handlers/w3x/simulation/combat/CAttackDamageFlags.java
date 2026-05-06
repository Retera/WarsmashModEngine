package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public class CAttackDamageFlags extends CBaseDamageFlags {

	public CAttackDamageFlags(boolean ranged) {
		super();
		this.setRanged(ranged);
	}
	
	private CAttackDamageFlags(CAttackDamageFlags base) {
		super(base);
	}
	
	@Override
	public CDamageFlags copy() {
		return new CAttackDamageFlags(this);
	}

	@Override
	public boolean isAttack() {
		return true;
	}

}
