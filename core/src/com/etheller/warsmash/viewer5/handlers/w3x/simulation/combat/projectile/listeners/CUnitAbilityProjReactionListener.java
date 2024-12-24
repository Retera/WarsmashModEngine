package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public interface CUnitAbilityProjReactionListener {
	public boolean onHit(final CSimulation simulation, CUnit source, CUnit target, CProjectile projectile);
}
