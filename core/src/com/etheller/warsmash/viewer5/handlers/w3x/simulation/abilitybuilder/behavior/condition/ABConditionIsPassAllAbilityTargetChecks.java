package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.widget.ABWidgetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsPassAllAbilityTargetChecks extends ABCondition {

	private ABUnitCallback caster;
	private ABWidgetCallback target;

	@Override
	public Boolean callback(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		CUnit theCaster = casterUnit;

		if (caster != null) {
			theCaster = caster.callback(game, casterUnit, localStore, castId);
		}

		final AbilityBuilderActiveAbility abil = (AbilityBuilderActiveAbility) localStore.get(ABLocalStoreKeys.ABILITY);
		final BooleanAbilityTargetCheckReceiver<CWidget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
				.<CWidget>getInstance().reset();

		// NOTE: below "theCaster.getPlayerIndex()" added in refactor, assumes all AB
		// actions are triggered by owning player! for neutral building sales/control
		// functions, we may with to disambiguate between the owner of the unit and the
		// player responsible for the command
		abil.checkCanTarget(game, theCaster, theCaster.getPlayerIndex(), abil.getBaseOrderId(),
				((Boolean) localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId))),
				target.callback(game, casterUnit, localStore, castId), booleanTargetReceiver);

		if (booleanTargetReceiver.isTargetable()) {
			return true;
		}
		return false;
	}

}
