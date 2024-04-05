package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTimedBuff implements ABAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showTimedLifeBar;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onExpireActions;
	private ABBooleanCallback showIcon;
	private CEffectType artType;
	private ABBooleanCallback hideArt;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		boolean showTimedLife = false;
		if (showTimedLifeBar != null) {
			showTimedLife = showTimedLifeBar.callback(game, caster, localStore, castId);
		}

		if (showIcon != null) {
			ABTimedBuff ability = new ABTimedBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId),
					duration.callback(game, caster, localStore, castId), showTimedLife, localStore, onAddActions,
					onRemoveActions, onExpireActions, showIcon.callback(game, caster, localStore, castId), castId);
			if (artType != null) {
				ability.setArtType(artType);
			}
			if (hideArt != null && hideArt.callback(game, caster, localStore, castId)) {
				ability.setArtType(null);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		} else {
			ABTimedBuff ability = new ABTimedBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId),
					duration.callback(game, caster, localStore, castId), showTimedLife, localStore, onAddActions,
					onRemoveActions, onExpireActions, castId);
			if (artType != null) {
				ability.setArtType(artType);
			}
			if (hideArt != null && hideArt.callback(game, caster, localStore, castId)) {
				ability.setArtType(null);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}
	}
}
