package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABActionCreateBuffFromId implements ABAction {

	private ABIDCallback abilityId;

	@SuppressWarnings("unchecked")
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		CAbility ability = game.getAbilityData().getBuffType(abilityId.callback(game, caster, localStore))
				.createAbility(game.getHandleIdAllocator().createId());
		((CAbilityAbilityBuilderBuff) ability).setParentLevelData(
				(List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA));
		((CAbilityAbilityBuilderBuff) ability).setParentCaster(caster);
		((CAbilityAbilityBuilderBuff) ability).setParentLocalStore(localStore);

		localStore.put(ABLocalStoreKeys.LASTCREATEDABILITY, ability);
	}
}
