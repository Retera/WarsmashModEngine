package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.floatingtext;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionCreateFloatingTextOnUnit implements ABAction {

	private ABUnitCallback target;
	private TextTagConfigType textType;
	private ABStringCallback message;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		game.spawnTextTag(target.callback(game, caster, localStore, castId), caster.getPlayerIndex(), textType,
				message.callback(game, caster, localStore, castId));
	}
}
