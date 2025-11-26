package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.list;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.ABListCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionLocationListAdd implements ABAction {

	private ABListCallback<AbilityPointTarget> list;
	private ABLocationCallback location;
	private ABIntegerCallback index;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final List<AbilityPointTarget> l = this.list.callback(game, caster, localStore, castId);
		if (index != null) {
			l.add(index.callback(game, caster, localStore, castId),
					location.callback(game, caster, localStore, castId));
		} else {
			l.add(location.callback(game, caster, localStore, castId));
		}
	}
}
