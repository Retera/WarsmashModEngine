package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.floatingtext;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionCreateNumericFloatingTextOnUnit implements ABAction {

	private ABUnitCallback target;
	private TextTagConfigType textType;
	private ABFloatCallback amount;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float theAmount = 0;
		if (amount!=null) {
			theAmount = amount.callback(game, caster, localStore, castId);
		}

		game.spawnTextTag(target.callback(game, caster, localStore, castId), caster.getPlayerIndex(), textType,
				(int) (theAmount));
	}
}
