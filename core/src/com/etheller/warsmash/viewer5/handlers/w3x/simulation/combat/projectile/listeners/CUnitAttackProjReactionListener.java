package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;

public interface CUnitAttackProjReactionListener {
	public boolean onHit(final CSimulation simulation, CUnit source, CUnit target, CAttackProjectile projectile);
}
