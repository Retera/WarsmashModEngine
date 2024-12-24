package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public interface CAbilityCollisionProjectileListener {
	void onLaunch(CSimulation game, CProjectile projectile, AbilityTarget target);

	void onPreHits(CSimulation game, CProjectile projectile, AbilityPointTarget location);
	
	boolean canHitTarget(CSimulation game, CWidget target);
	
	void onHit(CSimulation game, CProjectile projectile, AbilityTarget target);
	
	void setUnitTargets(int units);
	
	void setDestructableTargets(int dests);
	
	void setCurrentLocation(AbilityPointTarget loc);

	CAbilityCollisionProjectileListener DO_NOTHING = new CAbilityCollisionProjectileListener() {
		@Override
		public void onLaunch(CSimulation game, CProjectile projectile, AbilityTarget target) {
		}

		@Override
		public void onPreHits(CSimulation game, CProjectile projectile, AbilityPointTarget loc) {
		}

		@Override
		public void onHit(CSimulation game, CProjectile projectile, AbilityTarget target) {
		}

		@Override
		public void setUnitTargets(int units) {
		}

		@Override
		public void setDestructableTargets(int dests) {
		}

		@Override
		public void setCurrentLocation(AbilityPointTarget loc) {
		}

		@Override
		public boolean canHitTarget(CSimulation game, CWidget target) {
			return false;
		}
	};
}
