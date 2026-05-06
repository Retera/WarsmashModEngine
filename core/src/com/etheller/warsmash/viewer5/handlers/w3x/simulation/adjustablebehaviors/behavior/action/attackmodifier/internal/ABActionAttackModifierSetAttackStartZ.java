
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier.internal;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABActionAttackModifierSetAttackStartZ implements ABAction {

	private ABFloatCallback z;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnitAttackSettings settings = localStore
				.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKSETTINGS, castId), CUnitAttackSettings.class);
		if (z != null) {
			settings.setZ(z.callback(caster, localStore, castId));
		}
	}
}