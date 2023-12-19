package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public interface CUnitEffectReactionListener {
	public boolean onHit(final CSimulation simulation, CUnit source, AbilityTarget target, CAttackProjectile projectile);
	public boolean onHit(final CSimulation simulation, CUnit source, AbilityTarget target, CProjectile projectile);
}
