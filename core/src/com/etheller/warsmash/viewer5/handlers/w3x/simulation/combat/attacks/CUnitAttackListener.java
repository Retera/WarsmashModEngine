package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public interface CUnitAttackListener {
	void onLaunch();

	void onHit(AbilityTarget target, float damage);

	CUnitAttackListener DO_NOTHING = new CUnitAttackListener() {
		@Override
		public void onLaunch() {
		}

		@Override
		public void onHit(AbilityTarget target, float damage) {
		}
	};
}
