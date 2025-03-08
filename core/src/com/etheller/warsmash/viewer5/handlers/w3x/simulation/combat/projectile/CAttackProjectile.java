package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;

public abstract class CAttackProjectile extends CProjectile {
	protected float damage;
	protected final CUnitAttackListener attackListener;

	public CAttackProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			final CUnit source, final float damage, boolean homingEnabled,
			final CUnitAttackListener attackListener) {
		super(x, y, speed, target, homingEnabled, source);
		this.damage = damage;
		this.attackListener = attackListener;
	}

	public abstract CUnitAttack getUnitAttack();
	
	public float getDamage() {
		return this.damage;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
}
