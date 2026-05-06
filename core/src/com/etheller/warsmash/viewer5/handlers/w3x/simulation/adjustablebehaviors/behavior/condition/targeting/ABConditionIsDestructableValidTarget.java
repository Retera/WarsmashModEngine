package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.targeting;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABConditionIsDestructableValidTarget extends ABBooleanCallback {

	private ABUnitCallback caster;
	private ABDestructableCallback target;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean callback(CUnit casterUnit, ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = casterUnit;

		EnumSet<CTargetType> targetsAllowed = null;
		ABAbilityBuilderAbility ability = localStore.originAbility;
		if (ability != null && ability instanceof ABAbilityBuilderActiveAbility) {
			targetsAllowed = ((ABAbilityBuilderActiveAbility) ability).getTargetsAllowed();
		} else {
			List<ABAbilityBuilderAbilityTypeLevelData> levelData = (List<ABAbilityBuilderAbilityTypeLevelData>) localStore
					.get(ABLocalStoreKeys.LEVELDATA);
			targetsAllowed = levelData.get(localStore.getLevelDataInstanceLevel(castId)).getTargetsAllowed();
		}

		if (targetsAllowed.isEmpty()) {
			return true;
		}
		if (caster != null) {
			theCaster = caster.callback(casterUnit, localStore, castId);
		}

		return target.callback(casterUnit, localStore, castId).canBeTargetedBy(localStore.game, theCaster,
				targetsAllowed);
	}

}
