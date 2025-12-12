package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.abilities;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionAbilityRunEndCastingActions implements ABSingleAction {

	private ABAbilityCallback ability;
	private ABUnitCallback caster;
	
	private ABLocationCallback targetLoc;
	private ABUnitCallback targetUnit;
	private ABItemCallback targetItem;
	private ABDestructableCallback targetDest;

	private ABPlayerCallback orderingPlayer;

	@Override
	public void runAction(final CSimulation game, final CUnit originalCaster, final Map<String, Object> localStore,
			final int castId) {
		CUnit theCaster = originalCaster;
		if (caster != null) {
			theCaster = caster.callback(game, originalCaster, localStore, castId);
		}
		int theOrderingPlayerIndex;
		if (orderingPlayer != null) {
			theOrderingPlayerIndex = orderingPlayer.callback(game, theCaster, localStore, castId).getId();
		} else {
			theOrderingPlayerIndex = theCaster.getPlayerIndex();
		}
		
		AbilityTarget tar = null;
		if (targetLoc != null) {
			tar = targetLoc.callback(game, originalCaster, localStore, castId);
		} else if (targetUnit != null) {
			tar = targetUnit.callback(game, originalCaster, localStore, castId);
		} else if (targetItem != null) {
			tar = targetItem.callback(game, originalCaster, localStore, castId);
		} else if (targetDest != null) {
			tar = targetDest.callback(game, originalCaster, localStore, castId);
		}
		
		final CAbility theAbility = this.ability.callback(game, originalCaster, localStore, castId);
		if (theAbility instanceof AbilityBuilderActiveAbility) {
			AbilityBuilderActiveAbility active = ((AbilityBuilderActiveAbility) theAbility);
			int orderId = active.getBaseOrderId();

			active.internalBegin(game, theCaster, theOrderingPlayerIndex, orderId, false, tar);
			active.runEndCastingActions(game, theCaster, orderId);
			active.cleanupInputs();
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
