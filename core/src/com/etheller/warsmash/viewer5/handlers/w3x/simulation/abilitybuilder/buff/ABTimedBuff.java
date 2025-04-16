package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABTimedBuff extends ABGenericTimedBuff {

	protected Map<String, Object> localStore;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onExpireActions;

	private CEffectType artType = CEffectType.TARGET;
	private NonStackingFx fx;
	private SimulationRenderComponent sfx;
	private SimulationRenderComponent lsfx;

	protected int castId = 0;

	public ABTimedBuff(int handleId, War3ID alias, CAbility sourceAbility, CUnit sourceUnit, float duration, boolean showTimedLifeBar,
			Map<String, Object> localStore, List<ABAction> onAddActions, List<ABAction> onRemoveActions,
			List<ABAction> onExpireActions, boolean showIcon, final int castId, final boolean leveled,
			final boolean positive, final boolean dispellable) {
		this(handleId, alias, sourceAbility, sourceUnit, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions,
				castId, leveled, positive, dispellable);
		this.setIconShowing(showIcon);
	}

	public ABTimedBuff(int handleId, War3ID alias, CAbility sourceAbility, CUnit sourceUnit, float duration, boolean showTimedLifeBar,
			Map<String, Object> localStore, List<ABAction> onAddActions, List<ABAction> onRemoveActions,
			List<ABAction> onExpireActions, final int castId, final boolean leveled, final boolean positive,
			final boolean dispellable) {
		super(handleId, alias, sourceAbility, sourceUnit, duration, showTimedLifeBar, leveled, positive, dispellable);
		this.localStore = localStore;
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
		this.onExpireActions = onExpireActions;
		this.castId = castId;
		
		this.setLevel(null, null, (int) localStore.getOrDefault(ABLocalStoreKeys.CURRENTLEVEL, 1));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUniqueValue(String key, Class<T> cls) {
		Object o = this.localStore.get(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
		if (o != null && o.getClass() == cls) {
			return (T)o;
		}
		return null;
	}
	
	public void addUniqueValue(Object item, String key) {
		this.localStore.put(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()), item);
	}
	
	public void removeUniqueValue(String key) {
		this.localStore.remove(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
	}

	public void cleanUpUniqueValues() {
		final Set<String> keySet = new HashSet<>(localStore.keySet());
		String search = ABLocalStoreKeys.combineUniqueValueKey("", this.getHandleId());
		for (final String key : keySet) {
			if (key.contains(search)) {
				localStore.remove(key);
			}
		}
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

}
