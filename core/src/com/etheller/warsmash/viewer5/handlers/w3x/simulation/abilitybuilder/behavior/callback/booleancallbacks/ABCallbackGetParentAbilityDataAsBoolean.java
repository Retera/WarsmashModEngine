package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetParentAbilityDataAsBoolean extends ABBooleanCallback {

	private ABIntegerCallback dataField;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.PARENTLEVELDATA);
		final int parentLevel = (int) ((Map<String, Object>) localStore.get(ABLocalStoreKeys.PARENTLOCALSTORE))
				.get(ABLocalStoreKeys.CURRENTLEVEL);

		final String data = levelData.get(parentLevel - 1).getData()
				.get(this.dataField.callback(game, caster, localStore, castId));
		final int parsedData = Integer.parseInt(data);
		return parsedData == 1;
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "GetAbilityDataAsBooleanAU(" + jassTextGenerator.getTriggerLocalStore() + ", ConvertDataFieldLetter("
				+ this.dataField.generateJassEquivalent(jassTextGenerator) + "))";
	}

}
