package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.widget.ABWidgetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsPassAllAbilityTargetChecks extends ABBooleanCallback {

	private ABUnitCallback caster;
	private ABWidgetCallback target;

	@Override
	public Boolean callback(CUnit casterUnit, ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = casterUnit;

		if (caster != null) {
			theCaster = caster.callback(casterUnit, localStore, castId);
		}

		ABAbilityBuilderActiveAbility abil = (ABAbilityBuilderActiveAbility) localStore.originAbility;
		final BooleanAbilityTargetCheckReceiver<CWidget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
				.<CWidget>getInstance().reset();

		abil.checkCanTarget(localStore.game, theCaster, abil.getBaseOrderId(),
				(localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), Boolean.class)),
				target.callback(casterUnit, localStore, castId), booleanTargetReceiver);

		if (booleanTargetReceiver.isTargetable()) {
			return true;
		}
		return false;
	}

}
