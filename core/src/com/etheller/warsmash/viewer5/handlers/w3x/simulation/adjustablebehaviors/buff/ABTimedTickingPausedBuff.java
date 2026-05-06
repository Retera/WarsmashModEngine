package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABTimedTickingPausedBuff extends ABTimedBuff implements CPausedTickingBuff {

	private List<ABAction> onTickActions;

	public ABTimedTickingPausedBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onExpireActions, List<ABAction> onTickActions,
			boolean showIcon, final int castId, final boolean leveled, final boolean positive,
			final boolean dispellable) {
		this(handleId, alias, localStore, sourceAbility, sourceUnit, duration, showTimedLifeBar, onAddActions,
				onRemoveActions, onExpireActions, onTickActions, castId, leveled, positive, dispellable);
		this.setIconShowing(showIcon);
	}

	public ABTimedTickingPausedBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onExpireActions, List<ABAction> onTickActions,
			final int castId, final boolean leveled, final boolean positive, final boolean dispellable) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, showTimedLifeBar, onAddActions,
				onRemoveActions, onExpireActions, castId, leveled, positive, dispellable);
		this.onTickActions = onTickActions;
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (onTickActions != null) {
			localStore.put(ABLocalStoreKeys.BUFF, this);
			for (ABAction action : onTickActions) {
				action.runAction(unit, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.BUFF);
		}
		if (!unit.isPaused()) {
			super.onTick(game, unit);
		}
	}
}
