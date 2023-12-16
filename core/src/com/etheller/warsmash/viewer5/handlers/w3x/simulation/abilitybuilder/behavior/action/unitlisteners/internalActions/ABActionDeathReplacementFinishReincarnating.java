
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalActions;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionDeathReplacementFinishReincarnating implements ABAction {

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		caster.setFalseDeath(false);
		game.getWorldCollision().addUnit(caster);
		caster.getUnitAnimationListener().playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
	}
}