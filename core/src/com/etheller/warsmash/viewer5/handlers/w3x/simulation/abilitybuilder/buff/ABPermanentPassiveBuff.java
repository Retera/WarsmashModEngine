package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABPermanentPassiveBuff extends ABGenericPermanentBuff {

	protected Map<String, Object> localStore;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	
	private boolean showFx = true;
	private boolean playSfx = false;
	
	private CEffectType artType = CEffectType.TARGET;
	private NonStackingFx fx;
	private SimulationRenderComponent sfx;
	private SimulationRenderComponent lsfx;
	
	protected int castId = 0;

	public ABPermanentPassiveBuff(int handleId, War3ID alias, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, boolean showIcon, final int castId) {
		this(handleId, alias, localStore, onAddActions, onRemoveActions, castId);
		this.setIconShowing(showIcon);
	}

	public ABPermanentPassiveBuff(int handleId, War3ID alias, Map<String, Object> localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, final int castId) {
		super(handleId, alias);
		this.localStore = localStore;
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
		this.castId = castId;
	}
	
	public void setArtType(CEffectType artType) {
		this.artType = artType;
	}
	
	public void setShowFx(boolean showFx) {
		this.showFx = showFx;
	}
	
	public void setPlaySfx(boolean playSfx) {
		this.playSfx = playSfx;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		if(this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.getAlias() != null) {
			if (showFx) {
				this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), artType);
			}
			if (playSfx) {
				this.sfx = game.unitSoundEffectEvent(unit, getAlias());
				this.lsfx = game.unitLoopSoundEffectEvent(unit, getAlias());
			}
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
		if (this.sfx != null) {
			this.sfx.remove();
		}
		if (this.lsfx != null) {
			this.lsfx.remove();
		}
		if (onRemoveActions != null) {
			for (ABAction action : onRemoveActions) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	
	
	
}
