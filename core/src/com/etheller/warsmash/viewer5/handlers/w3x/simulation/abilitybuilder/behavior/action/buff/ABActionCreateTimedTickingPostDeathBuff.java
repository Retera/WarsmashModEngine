package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTickingPostDeathBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionCreateTimedTickingPostDeathBuff implements ABAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showTimedLifeBar;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onTickActions;
	private ABBooleanCallback showIcon;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		boolean showTimedLife = false;
		if (showTimedLifeBar != null) {
			showTimedLife = showTimedLifeBar.callback(game, caster, localStore);
		}

		if (showIcon != null) {
			CBuff ability = new ABTimedTickingPostDeathBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore), duration.callback(game, caster, localStore),
					showTimedLife, localStore, onAddActions, onRemoveActions, onTickActions,
					showIcon.callback(game, caster, localStore));

			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		} else {
			CBuff ability = new ABTimedTickingPostDeathBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore), duration.callback(game, caster, localStore),
					showTimedLife, localStore, onAddActions, onRemoveActions, onTickActions);

			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}

	}
}
