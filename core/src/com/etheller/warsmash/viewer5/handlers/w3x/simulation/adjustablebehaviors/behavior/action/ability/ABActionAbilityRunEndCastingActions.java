package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionAbilityRunEndCastingActions implements ABSingleAction {

	private ABAbilityCallback ability;
	private ABUnitCallback caster;

	private ABLocationCallback targetLoc;
	private ABUnitCallback targetUnit;
	private ABItemCallback targetItem;
	private ABDestructableCallback targetDest;

	@Override
	public void runAction(final CUnit originalCaster, final ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = originalCaster;
		if (caster != null) {
			theCaster = caster.callback(originalCaster, localStore, castId);
		}

		AbilityTarget tar = null;
		if (targetLoc != null) {
			tar = targetLoc.callback(originalCaster, localStore, castId);
		} else if (targetUnit != null) {
			tar = targetUnit.callback(originalCaster, localStore, castId);
		} else if (targetItem != null) {
			tar = targetItem.callback(originalCaster, localStore, castId);
		} else if (targetDest != null) {
			tar = targetDest.callback(originalCaster, localStore, castId);
		}

		final CAbility theAbility = this.ability.callback(originalCaster, localStore, castId);
		if (theAbility instanceof ABAbilityBuilderActiveAbility) {
			ABAbilityBuilderActiveAbility active = ((ABAbilityBuilderActiveAbility) theAbility);
			int orderId = active.getBaseOrderId();

			active.internalBegin(localStore.game, theCaster, orderId, false, tar);
			active.runEndCastingActions(localStore.game, theCaster, orderId);
			active.cleanupInputs();
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
