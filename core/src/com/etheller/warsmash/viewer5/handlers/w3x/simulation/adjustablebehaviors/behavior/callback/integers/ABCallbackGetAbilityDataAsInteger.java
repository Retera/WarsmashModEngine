package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template.ABDataFieldLetter;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;

public class ABCallbackGetAbilityDataAsInteger extends ABIntegerCallback {

	private ABDataFieldLetter dataField;

	@SuppressWarnings("unchecked")
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final List<ABAbilityBuilderAbilityTypeLevelData> levelData = (List<ABAbilityBuilderAbilityTypeLevelData>) localStore
				.get(ABLocalStoreKeys.LEVELDATA);

		final String data = levelData.get(localStore.getLevelDataInstanceLevel(castId)).getData()
				.get(this.dataField.getIndex());
		if ("-".equals(data)) {
			return 0;
		}
		return Integer.parseInt(data);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "GetAbilityDataAsIntegerAU(" + jassTextGenerator.getTriggerLocalStore() + ", DATA_FIELD_LETTER_"
				+ this.dataField.name() + ")";
	}

}
