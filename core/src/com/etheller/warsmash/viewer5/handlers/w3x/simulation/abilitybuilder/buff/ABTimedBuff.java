package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABTimedBuff extends CBuffTimed {
	private boolean showTimedLifeBar;

	protected Map<String, Object> localStore;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;

	public ABTimedBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, boolean showIcon) {
		this(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions);
		this.setIconShowing(showIcon);
	}

	public ABTimedBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions) {
		super(handleId, alias, duration);
		this.localStore = localStore;
		this.showTimedLifeBar = showTimedLifeBar;
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
	}

	@Override
	public boolean isTimedLifeBar() {
		return showTimedLifeBar;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		if (onAddActions != null) {
			for (ABAction action : onAddActions) {
				action.runAction(game, unit, localStore);
			}
		}
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		if (onRemoveActions != null) {
			for (ABAction action : onRemoveActions) {
				action.runAction(game, unit, localStore);
			}
		}
	}

}
