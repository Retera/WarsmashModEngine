package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTickingBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTimedTickingBuff implements ABAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showTimedLifeBar;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onExpireActions;
	private List<ABAction> onTickActions;
	private ABBooleanCallback showIcon;
	private CEffectType artType;

	private ABBooleanCallback leveled;
	private ABBooleanCallback positive;
	private ABBooleanCallback dispellable;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		boolean showTimedLife = false;
		if (showTimedLifeBar != null) {
			showTimedLife = showTimedLifeBar.callback(game, caster, localStore, castId);
		}
		boolean isLeveled = false;
		if (leveled != null) {
			isLeveled = leveled.callback(game, caster, localStore, castId);
		} else {
			isLeveled = (boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYLEVELED, false);
		}
		boolean isPositive = true;
		if (positive != null) {
			isPositive = positive.callback(game, caster, localStore, castId);
		}
		boolean isDispellable = true;
		if (dispellable != null) {
			isDispellable = dispellable.callback(game, caster, localStore, castId);
		} else {
			isDispellable = !((boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYPHYSICAL, false));
		}

		if (showIcon != null) {
			ABTimedTickingBuff ability = new ABTimedTickingBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId),
					duration.callback(game, caster, localStore, castId), showTimedLife, localStore, onAddActions,
					onRemoveActions, onExpireActions, onTickActions,
					showIcon.callback(game, caster, localStore, castId), castId, isLeveled, isPositive, isDispellable);
			if (artType != null) {
				ability.setArtType(artType);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		} else {
			ABTimedTickingBuff ability = new ABTimedTickingBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId),
					duration.callback(game, caster, localStore, castId), showTimedLife, localStore, onAddActions,
					onRemoveActions, onExpireActions, onTickActions, castId, isLeveled, isPositive, isDispellable);
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
