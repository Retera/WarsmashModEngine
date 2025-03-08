package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.DataFieldLetter;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetAbilityDataAsID extends ABIDCallback {

	private DataFieldLetter dataField;

	@SuppressWarnings("unchecked")
	@Override
	public War3ID callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final List<CAbilityTypeAbilityBuilderLevelData> levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);
		final int level = (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL);

		final String data = levelData.get(level - 1).getData().get(this.dataField.getIndex());
		if ((data == null) || "-".equals(data) || data.isBlank() || "_".equals(data)) {
			return War3ID.NONE;
		}
		return War3ID.fromString(data);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "GetAbilityDataAsIDAU(" + jassTextGenerator.getTriggerLocalStore() + ", DATA_FIELD_LETTER_"
				+ this.dataField.name() + ")";
	}

}
