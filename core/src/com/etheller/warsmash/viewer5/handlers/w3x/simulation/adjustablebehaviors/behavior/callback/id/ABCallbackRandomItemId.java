package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABRandomTypeHandler;

public class ABCallbackRandomItemId extends ABIDCallback {

	private ABIDCallback previousId;
	private ABIntegerCallback level;
	private ABBooleanCallback ignoreValidFlag;
	private ABBooleanCallback ignoreRandomFlag;

	@Override
	public War3ID callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		if (previousId == null && level == null && ignoreValidFlag == null && ignoreRandomFlag == null) {
			return ABRandomTypeHandler.getRandomItemType(localStore.game).getTypeId();
		}

		War3ID id = null;
		int lv = -1;
		boolean valid = true;
		boolean random = false;
		if (previousId != null) {
			id = previousId.callback(caster, localStore, castId);
		}
		if (level != null) {
			lv = level.callback(caster, localStore, castId);
		}
		if (ignoreValidFlag != null) {
			valid = ignoreValidFlag.callback(caster, localStore, castId);
		}
		if (ignoreRandomFlag != null) {
			random = ignoreRandomFlag.callback(caster, localStore, castId);
		}
		CItemType type = ABRandomTypeHandler.getRandomItemType(localStore.game, id, lv, valid, random);
		if (type == null) {
			return null;
		}
		return type.getTypeId();
	}

}
