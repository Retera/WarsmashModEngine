package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;

public class CAttackProjectileMissile extends CAttackProjectile {
	protected final CUnitAttackMissile unitAttack;
	protected final int bounceIndex;

	public CAttackProjectileMissile(final float x, final float y, final float speed, final AbilityTarget target,
			final CUnit source, final float damage, final CUnitAttackMissile unitAttack, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		super(x, y, speed, target, source, damage, unitAttack.isProjectileHomingEnabled(), attackListener);
		this.unitAttack = unitAttack;
		this.bounceIndex = bounceIndex;
	}

	@Override
	protected void onHitTarget(CSimulation game) {
		CUnit tarU = getTarget().visit(AbilityTargetVisitor.UNIT);
		if (tarU == null || tarU.checkForAttackProjReaction(game, getSource(), this)) {
			this.unitAttack.doDamage(game, getSource(), getTarget(), this.damage, getX(), getY(), this.bounceIndex,
					this.attackListener);
		}
		
	}

	public CUnitAttack getUnitAttack() {
		return this.unitAttack;
	}
}
