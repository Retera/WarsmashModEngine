package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABPermanentPassiveBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreatePassiveBuff implements ABAction {

	private ABIDCallback buffId;
	private ABBooleanCallback showIcon;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private CEffectType artType;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		if (showIcon != null) {
			ABPermanentPassiveBuff ability = new ABPermanentPassiveBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId), localStore, onAddActions, onRemoveActions, showIcon.callback(game, caster, localStore, castId), castId);
			if (artType != null) {
				ability.setArtType(artType);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		} else {
			ABPermanentPassiveBuff ability = new ABPermanentPassiveBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId), localStore, onAddActions, onRemoveActions, true, castId);
			if (artType != null) {
				ability.setArtType(artType);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}
	}
}
