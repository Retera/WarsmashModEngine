package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.item;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionTransformItem implements ABAction {

	private ABItemCallback item;
	private ABIDCallback newType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CItem it = this.item.callback(game, caster, localStore, castId);
		War3ID type = newType.callback(game, caster, localStore, castId);
		it.setTypeId(game, type);
	}
}
