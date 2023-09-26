package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;

public abstract class AbstractCAbility implements CAbility {
	private final int handleId;
	private boolean disabled = false;
	private boolean iconShowing = true;
	private boolean permanent = false;
	
	private War3ID code;

	public AbstractCAbility(final int handleId, final War3ID code) {
		this.handleId = handleId;
		this.code = code;
	}

	@Override
	public final int getHandleId() {
		return this.handleId;
	}
	
	public War3ID getCode() { 
		return this.code;
	}
	
	@Override
	public War3ID getAlias() {
		return this.getCode();
	}

	@Override
	public final boolean isDisabled() {
		return this.disabled;
	}

	@Override
	public final void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public final boolean isIconShowing() {
		return this.iconShowing;
	}

	@Override
	public final void setIconShowing(final boolean iconShowing) {
		this.iconShowing = iconShowing;
	}

	@Override
	public boolean isPermanent() {
		return this.permanent;
	}

	@Override
	public void setPermanent(final boolean permanent) {
		this.permanent = permanent;
	}

	@Override
	public final void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (this.disabled) {
			receiver.disabled();
		}
		else {
			innerCheckCanUse(game, unit, orderId, receiver);
		}
	}

	protected abstract void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver);

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
	}
}
