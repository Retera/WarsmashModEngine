package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;

public abstract class AbstractCAbility extends CExtensibleHandleAbstract implements CAbility {
	private final int handleId;
	private byte disabled = 0;
	private boolean hero = false;
	private boolean iconShowing = true;
	private boolean permanent = false;

	private final War3ID code;

	public AbstractCAbility(final int handleId, final War3ID code) {
		this.handleId = handleId;
		this.code = code;
	}

	@Override
	public final int getHandleId() {
		return this.handleId;
	}

	@Override
	public War3ID getCode() {
		return this.code;
	}

	@Override
	public War3ID getAlias() {
		return getCode();
	}

	@Override
	public boolean isHero() {
		return hero;
	}

	public void setHero(boolean hero) {
		this.hero = hero;
	}

	@Override
	public final boolean isDisabled() {
		return this.disabled != 0;
	}

	protected void onSetDisabled(final boolean disabled, final boolean wasDisabled, final CAbilityDisableType type) {

	}

	@Override
	public final void setDisabled(final boolean disabled, final CAbilityDisableType type) {
		boolean wasDisabled = this.isDisabled();
		if (disabled) {
			this.disabled |= type.getMask();
		}
		else {
			this.disabled &= ~type.getMask();
		}
		onSetDisabled(disabled, wasDisabled, type);
	}
	
	@Override
	public boolean isClickDisabled() {
		return this.disabled != 0;
	}
	
	@Override
	public void setClickDisabled(final boolean disabled, final CAbilityDisableType type) {
	}

	@Override
	public final boolean isIconShowing() {
		return this.iconShowing;
	}

	protected void onSetIconShowing(final boolean iconShowing) {

	}

	@Override
	public final void setIconShowing(final boolean iconShowing) {
		this.iconShowing = iconShowing;
		onSetIconShowing(iconShowing);
	}

	@Override
	public final boolean isPermanent() {
		return this.permanent;
	}

	protected void onSetPermanent(final boolean permanent) {

	}

	@Override
	public final void setPermanent(final boolean permanent) {
		this.permanent = permanent;
		onSetPermanent(permanent);
	}

	@Override
	public void setItemAbility(final CItem item, final int slot) {
		// do nothing
	}

	@Override
	public CItem getItem() {
		// do nothing
		return null;
	}
	
	@Override
	public boolean hasUniqueFlag(String flag) {
		return false;
	}
	
	public void addUniqueFlag(String flag) {
	}
	
	public void removeUniqueFlag(String flag) {
	}
	
	@Override
	public <T> T getUniqueValue(String key, Class<T> cls) {
		return null;
	}
	
	public void addUniqueValue(Object item, String key) {
	}
	
	public void removeUniqueValue(String key) {
	}

	@Override
	public final void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			boolean autoOrder, final AbilityActivationReceiver receiver) {
		if (isDisabled()) {
			receiver.disabled();
			checkRequirementsMet(game, unit, receiver);
			innerCheckCooldownDisabled(game,unit,orderId,receiver);
		}
		else {
			innerCheckCanUse(game, unit, orderId, receiver);
		}
	}

	protected void innerCheckCooldownDisabled(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		//do nothing
	}

	protected abstract void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver);

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public void checkRequirementsMet(final CSimulation game, final CUnit unit,
			final AbilityActivationReceiver receiver) {

	}

	@Override
	public boolean isRequirementsMet(final CSimulation game, final CUnit unit) {
		return true;
	}

	@Override
	public void onAddDisabled(final CSimulation game, final CUnit unit) {
		// do nothing
	}

	@Override
	public void onRemoveDisabled(final CSimulation game, final CUnit unit) {
		// do nothing
	}
}
