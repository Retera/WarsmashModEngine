package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class CAttackProjectileInstant extends CAttackProjectile {
	CUnitAttackInstant attack;
	private CUnitAttackSettings settings;

	public CAttackProjectileInstant(final float x, final float y, final AbilityTarget target,
			final CUnit source, final float damage, final CUnitAttackInstant unitAttack,
			final CUnitAttackListener attackListener, final CUnitAttackSettings settings) {
		super(x, y, Float.MAX_VALUE, target, source, damage, true, attackListener);
		this.attack = unitAttack;
		this.settings = settings;
	}
	@Override
	public boolean update(final CSimulation game) {
		final float tx = getTargetX();
		final float ty = getTargetY();

		this.x = tx;
		this.y = ty;

		if (!this.done) {
			this.done = true;
			this.onHitTarget(game);
		}
		return this.done;
	}

	@Override
	protected void onHitTarget(CSimulation game) {
		CUnit tarU = getTarget().visit(AbilityTargetVisitor.UNIT);
		if (tarU == null || tarU.checkForAttackProjReaction(game, getSource(), this)) {
			this.attack.doDamage(game, getSource(), getTarget(), this.damage, getX(), getY(),
					this.attackListener, settings);
		}
	}

	public CUnitAttack getUnitAttack() {
		return this.attack;
	}
	
	public float getDamage() {
		return this.damage;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
}
