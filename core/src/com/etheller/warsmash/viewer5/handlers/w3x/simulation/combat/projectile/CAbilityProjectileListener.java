package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public interface CAbilityProjectileListener {
	void onLaunch(CSimulation game, AbilityTarget target);

	void onHit(CSimulation game, AbilityTarget target);
}
