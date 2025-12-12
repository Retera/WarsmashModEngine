package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public class CAttackDamageFlags extends CBaseDamageFlags {

	public CAttackDamageFlags(boolean ranged) {
		super();
		this.setRanged(ranged);
	}

	@Override
	public boolean isAttack() {
		return true;
	}

}
