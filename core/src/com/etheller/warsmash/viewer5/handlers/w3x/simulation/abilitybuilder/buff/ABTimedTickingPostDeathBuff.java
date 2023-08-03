package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABTimedTickingPostDeathBuff extends ABTimedTickingBuff {

	public ABTimedTickingPostDeathBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onTickActions, boolean showIcon) {
		this(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onTickActions);
		this.setIconShowing(showIcon);
	}
	
	public ABTimedTickingPostDeathBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onTickActions) {
		super(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onTickActions);
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
	}

}
