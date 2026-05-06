package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABDestructableBuff implements CDestructableBuff {

	protected static int TIMEDLIFE = 0b1;
	protected static int NEGATIVE = 0b10;
	protected static int DISPELLABLE = 0b100;
	protected static int HERO = 0b1000;
	protected static int MAGIC = 0b10000;
	protected static int PHYSICAL = 0b100000;
	protected static int AURA = 0b1000000;
	protected int flags = 0b0;

	private int handleId;
	private War3ID alias;
	private int level;

	protected ABLocalDataStore localStore;
	protected int castId = 0;
	private CUnit caster;

	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onDeathActions;

	public ABDestructableBuff(int handleId, War3ID alias, int level, ABLocalDataStore localStore,
			List<ABAction> onAddActions, List<ABAction> onRemoveActions, List<ABAction> onDeathActions,
			final int castId, CUnit caster, boolean dispellable) {
		this.handleId = handleId;
		this.alias = alias;
		this.level = level;
		this.castId = castId;
		this.localStore = localStore;
		this.onAddActions = onAddActions;
		this.onRemoveActions = onRemoveActions;
		this.onDeathActions = onDeathActions;
		this.caster = caster;
		this.setDispellable(dispellable);
		this.setHero(caster.isHero());
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
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BUFFEDDEST, castId), this);
		if (onAddActions != null) {
			for (ABAction action : onAddActions) {
				action.runAction(this.caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BUFFEDDEST, castId));
	}

	@Override
	public void onRemove(CSimulation game, CDestructable dest) {
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BUFFEDDEST, castId), this);
		if (onRemoveActions != null) {
			for (ABAction action : onRemoveActions) {
				action.runAction(this.caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BUFFEDDEST, castId));
	}

	@Override
	public void onDeath(CSimulation game, CDestructable dest) {
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BUFFEDDEST, castId), this);
		if (onDeathActions != null) {
			for (ABAction action : onDeathActions) {
				action.runAction(this.caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BUFFEDDEST, castId));
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	public void setTimedLife(boolean timedLife) {
		this.flags = timedLife ? this.flags | TIMEDLIFE : this.flags & ~TIMEDLIFE;
	}

	@Override
	public boolean isTimedLife() {
		return ((this.flags & TIMEDLIFE) != 0);
	}

	public void setPositive(boolean positive) {
		this.flags = positive ? this.flags & ~NEGATIVE : this.flags | NEGATIVE;
	}

	@Override
	public boolean isPositive() {
		return ((this.flags & NEGATIVE) == 0);
	}

	public void setDispellable(boolean dispellable) {
		this.flags = dispellable ? this.flags | DISPELLABLE : this.flags & ~DISPELLABLE;
	}

	@Override
	public boolean isDispellable() {
		return ((this.flags & DISPELLABLE) != 0);
	}

	public void setHero(boolean hero) {
		this.flags = hero ? this.flags | HERO : this.flags & ~HERO;
	}

	@Override
	public boolean isHero() {
		return ((this.flags & HERO) != 0);
	}

	public void setMagic(boolean magic) {
		this.flags = magic ? this.flags | MAGIC : this.flags & ~MAGIC;
	}

	@Override
	public boolean isMagic() {
		return ((this.flags & MAGIC) != 0);
	}

	public void setPhysical(boolean physical) {
		this.flags = physical ? this.flags | PHYSICAL : this.flags & ~PHYSICAL;
	}

	@Override
	public boolean isPhysical() {
		return ((this.flags & PHYSICAL) != 0);
	}

	public void setAura(boolean aura) {
		this.flags = aura ? this.flags | AURA : this.flags & ~AURA;
	}

	@Override
	public boolean isAura() {
		return ((this.flags & AURA) != 0);
	}

}
