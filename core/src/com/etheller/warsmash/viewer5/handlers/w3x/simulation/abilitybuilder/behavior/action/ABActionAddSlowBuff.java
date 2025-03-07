package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffSlow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionAddSlowBuff implements ABSingleAction {

	private ABUnitCallback unit;
	private ABFloatCallback duration;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CAbility ability = new CBuffSlow(game.getHandleIdAllocator().createId(), War3ID.fromString("Bfro"),
				this.duration.callback(game, caster, localStore, castId),
				game.getGameplayConstants().getFrostAttackSpeedDecrease(),
				game.getGameplayConstants().getFrostMoveSpeedDecrease());
		this.unit.callback(game, caster, localStore, castId).add(game, ability);
		localStore.put(ABLocalStoreKeys.LASTADDEDBUFF, ability);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "AddUnitAbility(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", CreateSlowBuffAU("
				+ jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + "))";
	}
}
