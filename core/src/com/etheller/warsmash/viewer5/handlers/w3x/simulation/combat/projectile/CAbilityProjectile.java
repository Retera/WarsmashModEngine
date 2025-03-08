package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public class CAbilityProjectile extends CProjectile {
	private final CAbilityProjectileListener projectileListener;

	public CAbilityProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			boolean homingEnabled, final CUnit source, final CAbilityProjectileListener projectileListener) {
		super(x, y, speed, target, homingEnabled, source);
		this.projectileListener = projectileListener;
	}

	@Override
	protected void onHitTarget(CSimulation game) {
		projectileListener.onHit(game, this, getTarget());
	}
}
