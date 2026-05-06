package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABActionAbilityAttemptToApplyEffect implements ABSingleAction {

	private ABAbilityCallback ability;
	private ABUnitCallback source;
	private ABUnitCallback target;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = caster;
		if (source != null) {
			theCaster = source.callback(theCaster, localStore, castId);
		}
		CUnit theTarget = target.callback(theCaster, localStore, castId);
		final CAbility theAbility = this.ability.callback(caster, localStore, castId);
		if (theAbility instanceof ABAbilityBuilderActiveAbility) {
			ABAbilityBuilderActiveAbility active = ((ABAbilityBuilderActiveAbility) theAbility);
			int orderId = active.getBaseOrderId();
			final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;
			active.checkCanUse(localStore.game, theCaster, orderId, false, activationReceiver);
			if (activationReceiver.isOk()) {
				final BooleanAbilityTargetCheckReceiver<CWidget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<CWidget>getInstance().reset();
				active.checkCanTarget(
						localStore.game, theCaster, orderId, (localStore
								.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), Boolean.class)),
						theTarget, booleanTargetReceiver);
				if (booleanTargetReceiver.isTargetable()) {
					if (theCaster.chargeMana(active.getChargedManaCost())) {
						active.internalBegin(localStore.game, theCaster, orderId, false, theTarget);
						active.startCooldown(localStore.game, theCaster);
						active.runEndCastingActions(localStore.game, theCaster, orderId);
						active.cleanupInputs();
					}
				}
			}

		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
