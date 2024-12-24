package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABTimedTickingBuff extends ABTimedBuff {
	
	private List<ABAction> onTickActions;

	public ABTimedTickingBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onExpireActions, List<ABAction> onTickActions, boolean showIcon, final int castId) {
		this(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions, onTickActions, castId);
		this.setIconShowing(showIcon);
	}
	
	public ABTimedTickingBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onExpireActions, List<ABAction> onTickActions, final int castId) {
		super(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions, castId);
		this.onTickActions = onTickActions;
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (onTickActions != null) {
			for (ABAction action : onTickActions) {
				action.runAction(game, unit, localStore, castId);
			}
		}
		super.onTick(game, unit);
	}

}
