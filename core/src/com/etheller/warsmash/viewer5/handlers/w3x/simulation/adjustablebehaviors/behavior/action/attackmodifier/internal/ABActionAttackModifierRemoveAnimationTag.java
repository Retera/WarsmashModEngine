
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier.internal;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABActionAttackModifierRemoveAnimationTag implements ABAction {

	private ABStringCallback tag;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnitAttackSettings settings = localStore
				.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKSETTINGS, castId), CUnitAttackSettings.class);
		settings.removeAnimationName(SecondaryTag.valueOf(tag.callback(caster, localStore, castId)));
	}
}