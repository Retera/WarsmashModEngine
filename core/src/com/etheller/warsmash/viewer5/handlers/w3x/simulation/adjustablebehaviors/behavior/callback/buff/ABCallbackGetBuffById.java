package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetBuffById extends ABBuffCallback {

	private ABUnitCallback unit;
	private ABIDCallback id;

	@Override
	public CBuff callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = unit.callback(caster, localStore, castId);
		War3ID theId = id.callback(caster, localStore, castId);
		if (theUnit != null) {
			for (CAbility ability : theUnit.getAbilities()) {
				if (ability instanceof CBuff) {
					if (((CBuff) ability).getAlias() == theId) {
						return (CBuff) ability;
					}
				}
			}
		}
		return null;
	}

}
