package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABTimedTickingPostDeathBuff extends ABTimedTickingBuff {

	public ABTimedTickingPostDeathBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onExpireActions, List<ABAction> onTickActions, boolean showIcon, final int castId) {
		this(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions, onTickActions, castId);
		this.setIconShowing(showIcon);
	}
	
	public ABTimedTickingPostDeathBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onExpireActions, List<ABAction> onTickActions, final int castId) {
		super(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions, onTickActions, castId);
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
	}

}
