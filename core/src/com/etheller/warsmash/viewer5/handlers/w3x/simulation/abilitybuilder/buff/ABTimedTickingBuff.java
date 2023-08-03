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
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onTickActions, boolean showIcon) {
		this(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onTickActions);
		this.setIconShowing(showIcon);
	}
	
	public ABTimedTickingBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onTickActions) {
		super(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions);
		this.onTickActions = onTickActions;
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (onTickActions != null) {
			for (ABAction action : onTickActions) {
				action.runAction(game, unit, localStore);
			}
		}
		super.onTick(game, unit);
	}

}
