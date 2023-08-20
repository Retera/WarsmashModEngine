package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;

public class ABTimedBuff extends ABGenericTimedBuff {

	protected Map<String, Object> localStore;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	
	private NonStackingFx fx;
	
	protected int castId = 0;

	public ABTimedBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, boolean showIcon, final int castId) {
		this(handleId, alias, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, castId);
		this.setIconShowing(showIcon);
	}

	public ABTimedBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, final int castId) {
		super(handleId, alias, duration, showTimedLifeBar);
		this.localStore = localStore;
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
		this.castId = castId;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		if(this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.getAlias() != null) {
			this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), CEffectType.TARGET);
		}
		if (onAddActions != null) {
			for (ABAction action : onAddActions) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (onRemoveActions != null) {
			for (ABAction action : onRemoveActions) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	
	
	
}
