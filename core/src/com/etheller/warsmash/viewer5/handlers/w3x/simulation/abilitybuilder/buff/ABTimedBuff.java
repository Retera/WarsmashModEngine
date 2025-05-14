package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABTimedBuff extends ABGenericTimedBuff {

	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onExpireActions;

	private CEffectType artType = CEffectType.TARGET;
	private NonStackingFx fx;
	private SimulationRenderComponent sfx;
	private SimulationRenderComponent lsfx;

	protected int castId = 0;

	private List<StateModBuff> stateMods = null;
	private List<NonStackingStatBuff> statBuffs = null;

	public ABTimedBuff(int handleId, War3ID alias, Map<String, Object> localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onExpireActions, boolean showIcon, final int castId,
			final boolean leveled, final boolean positive, final boolean dispellable) {
		this(handleId, alias, localStore, sourceAbility, sourceUnit, duration, showTimedLifeBar, onAddActions,
				onRemoveActions, onExpireActions, castId, leveled, positive, dispellable);
		this.setIconShowing(showIcon);
	}

	public ABTimedBuff(int handleId, War3ID alias, Map<String, Object> localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onExpireActions, final int castId, final boolean leveled,
			final boolean positive, final boolean dispellable) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, showTimedLifeBar, leveled, positive,
				dispellable);
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
		this.onExpireActions = onExpireActions;
		this.castId = castId;

		this.setLevel(null, null, (int) localStore.getOrDefault(ABLocalStoreKeys.CURRENTLEVEL, 1));
	}

	public void setArtType(CEffectType artType) {
		this.artType = artType;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.getAlias() != null) {
			if (artType != null) {
				this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), artType);
			}
			this.sfx = game.unitSoundEffectEvent(unit, getAlias());
			this.lsfx = game.unitLoopSoundEffectEvent(unit, getAlias());
		}
		if (this.statBuffs != null) {
			for (NonStackingStatBuff buff : this.statBuffs) {
				unit.addNonStackingStatBuff(game, buff);
			}
		}
		if (this.stateMods != null) {
			for (StateModBuff mod : this.stateMods) {
				unit.addStateModBuff(mod);
			}
		}
		if (onAddActions != null) {
			localStore.put(ABLocalStoreKeys.BUFF, this);
			for (ABAction action : onAddActions) {
				action.runAction(game, unit, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.BUFF);
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
			localStore.put(ABLocalStoreKeys.BUFF, this);
			for (ABAction action : onRemoveActions) {
				action.runAction(game, unit, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.BUFF);
		}
		if (this.statBuffs != null) {
			for (NonStackingStatBuff buff : this.statBuffs) {
				unit.removeNonStackingStatBuff(game, buff);
			}
		}
		if (this.stateMods != null) {
			for (StateModBuff mod : this.stateMods) {
				unit.removeStateModBuff(mod);
			}
		}
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		if (onExpireActions != null) {
			localStore.put(ABLocalStoreKeys.BUFF, this);
			for (ABAction action : onExpireActions) {
				action.runAction(game, unit, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.BUFF);
		}
	}

	public void setStateMods(List<StateModBuff> stateMods) {
		this.stateMods = stateMods;
	}

	public void setStatBuffs(List<NonStackingStatBuff> statBuffs) {
		this.statBuffs = statBuffs;
	}

}
