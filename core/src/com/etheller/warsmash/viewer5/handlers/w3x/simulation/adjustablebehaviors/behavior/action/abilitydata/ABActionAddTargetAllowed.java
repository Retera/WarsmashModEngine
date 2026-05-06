package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.abilitydata;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABActionAddTargetAllowed implements ABSingleAction {

	private CTargetType targetType;

	@Override
	@SuppressWarnings("unchecked")
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CAbility ability = localStore.originAbility;
		if (ability != null && ability instanceof ABAbilityBuilderActiveAbility) {
			((ABAbilityBuilderActiveAbility) ability).getTargetsAllowed().add(this.targetType);
		} else {
			final List<ABAbilityBuilderAbilityTypeLevelData> levelData = (List<ABAbilityBuilderAbilityTypeLevelData>) localStore
					.get(ABLocalStoreKeys.LEVELDATA);
			final EnumSet<CTargetType> targetsAllowed = levelData
					.get(localStore.originAbility.getLevel() - 1).getTargetsAllowed();
			targetsAllowed.add(this.targetType);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "AbilityTypeLevelDataAddTargetAllowed("
				+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_LEVELDATA",
						JassTextGeneratorType.AbilityTypeLevelDataHandle)
				+ ", "
				+ jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_CURRENTLEVEL", JassTextGeneratorType.Integer)
				+ " - 1, TARGET_TYPE_" + this.targetType.name() + ")";
	}
}
