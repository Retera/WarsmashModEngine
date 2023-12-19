package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;

public class CAttackProjectile extends CProjectile {
	private final float damage;
	private final CUnitAttackMissile unitAttack;
	private final int bounceIndex;
	private final CUnitAttackListener attackListener;

	public CAttackProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			final CUnit source, final float damage, final CUnitAttackMissile unitAttack, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		super(x, y, speed, target, unitAttack.isProjectileHomingEnabled(), source);
		this.damage = damage;
		this.unitAttack = unitAttack;
		this.bounceIndex = bounceIndex;
		this.attackListener = attackListener;
	}

	@Override
	protected void onHitTarget(CSimulation game) {
		CUnit tarU = getTarget().visit(AbilityTargetVisitor.UNIT);
		if (tarU.checkForReaction(game, getSource(), this, true)) {
			this.unitAttack.doDamage(game, getSource(), getTarget(), this.damage, getX(), getY(), this.bounceIndex,
					this.attackListener);
		}
		
	}

	public CUnitAttackMissile getUnitAttack() {
		return this.unitAttack;
	}
}
