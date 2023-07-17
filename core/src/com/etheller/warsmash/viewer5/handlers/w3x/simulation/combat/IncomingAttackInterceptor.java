package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public interface IncomingAttackInterceptor {
	/**
	 * Called when an attack is about to launch. Returns false if the project will not be sent
	 *
	 * @param attackingUnit
	 * @param attack
	 * @param targetWidth
	 * @return
	 */
	boolean onLaunch(CUnit attackingUnit, CUnitAttack attack, CWidget targetWidth);
}
