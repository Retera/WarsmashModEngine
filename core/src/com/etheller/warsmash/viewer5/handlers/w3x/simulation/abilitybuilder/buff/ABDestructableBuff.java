package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABDestructableBuff implements CDestructableBuff {

	private int handleId;
	private War3ID alias;
	private int level;

	protected Map<String, Object> localStore;
	protected int castId = 0;
	private CUnit caster;

	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onDeathActions;

	public ABDestructableBuff(int handleId, War3ID alias, int level, Map<String, Object> localStore, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onDeathActions, final int castId, CUnit caster) {
		this.handleId = handleId;
		this.alias = alias;
		this.level = level;
		this.castId = castId;
		this.localStore = localStore;
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
		this.onDeathActions = onDeathActions;
		this.caster = caster;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	@Override
	public War3ID getAlias() {
		return this.alias;
	}

	@Override
	public void onAdd(CSimulation game, CDestructable dest) {
		localStore.put(ABLocalStoreKeys.BUFFEDDEST+castId, this);
		if (onAddActions != null) {
			for (ABAction action : onAddActions) {
				action.runAction(game, this.caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.BUFFEDDEST+castId);
	}

	@Override
	public void onRemove(CSimulation game, CDestructable dest) {
		localStore.put(ABLocalStoreKeys.BUFFEDDEST+castId, this);
		if (onRemoveActions != null) {
			for (ABAction action : onRemoveActions) {
				action.runAction(game, this.caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.BUFFEDDEST+castId);
	}

	@Override
	public void onDeath(CSimulation game, CDestructable dest) {
		localStore.put(ABLocalStoreKeys.BUFFEDDEST+castId, this);
		if (onDeathActions != null) {
			for (ABAction action : onDeathActions) {
				action.runAction(game, this.caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.BUFFEDDEST+castId);
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

}
