package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public abstract class ABBuff extends AbstractCBuff {
	protected final static int TIMEDLIFE = 0b1;
	protected final static int NEGATIVE = 0b10;
	protected final static int DISPELLABLE = 0b100;
	protected final static int LEVELED = 0b1000;
	protected final static int HERO = 0b10000;
	protected final static int PHYSICAL = 0b100000;
	protected final static int MAGIC = 0b1000000;
	protected final static int AURA = 0b10000000;
	protected final static int STACKS = 0b100000000;

	protected int flags = DISPELLABLE;

	protected ABLocalDataStore localStore;
	protected List<String> uniqueFlags = null;
	private String visibilityGroup = null;
	private CAbility sourceAbil;
	protected CUnit sourceUnit;

	public ABBuff(int handleId, War3ID code, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit) {
		super(handleId, code, alias);
		this.localStore = localStore;
		this.sourceAbil = sourceAbility;
		this.sourceUnit = sourceUnit;
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, boolean autoOrder,
			final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, boolean autoOrder,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId, boolean autoOrder) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public boolean hasUniqueFlag(String flag) {
		if (this.uniqueFlags != null) {
			return this.uniqueFlags.contains(flag);
		}
		return false;
	}

	public void addUniqueFlag(String flag) {
		if (this.uniqueFlags == null) {
			this.uniqueFlags = new ArrayList<>();
		}
		this.uniqueFlags.add(flag);
	}

	public void removeUniqueFlag(String flag) {
		if (this.uniqueFlags != null) {
			this.uniqueFlags.remove(flag);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUniqueValue(String key, Class<T> cls) {
		Object o = this.localStore.get(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
		if (o != null && o.getClass() == cls) {
			return (T) o;
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

	public void setVisibilityGroup(String visibilityGroup) {
		this.visibilityGroup = visibilityGroup;
	}

	@Override
	public String getVisibilityGroup() {
		return this.visibilityGroup;
	}

	public void setTimedLifeBar(boolean timedLife) {
		this.flags = timedLife ? this.flags | TIMEDLIFE : this.flags & ~TIMEDLIFE;
	}

	@Override
	public boolean isTimedLifeBar() {
		return ((this.flags & TIMEDLIFE) != 0);
	}

	public void setPositive(boolean positive) {
		this.flags = positive ? this.flags & ~NEGATIVE : this.flags | NEGATIVE;
	}

	@Override
	public boolean isPositive() {
		return ((this.flags & NEGATIVE) == 0);
	}

	public void setLeveled(boolean leveled) {
		this.flags = leveled ? this.flags | LEVELED : this.flags & ~LEVELED;
	}

	@Override
	public boolean isLeveled() {
		return ((this.flags & LEVELED) != 0);
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

	public void setPhysical(boolean physical) {
		this.flags = physical ? this.flags | PHYSICAL : this.flags & ~PHYSICAL;
	}

	@Override
	public boolean isPhysical() {
		return ((this.flags & PHYSICAL) != 0);
	}

	public void setMagic(boolean magic) {
		this.flags = magic ? this.flags | MAGIC : this.flags & ~MAGIC;
	}

	@Override
	public boolean isMagic() {
		return ((this.flags & MAGIC) != 0);
	}

	public void setAura(boolean aura) {
		this.flags = aura ? this.flags | AURA : this.flags & ~AURA;
	}

	@Override
	public boolean isAura() {
		return ((this.flags & AURA) != 0);
	}

	public void setStacks(boolean stacks) {
		this.flags = stacks ? this.flags | STACKS : this.flags & ~STACKS;
	}

	public boolean isStacks() {
		return ((this.flags & STACKS) != 0);
	}

	@Override
	public CAbility getSourceAbility() {
		return this.sourceAbil;
	}

	@Override
	public CUnit getSourceUnit() {
		return this.sourceUnit;
	}
}
