package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public abstract class AbstractGenericSingleIconActiveAbility extends AbstractGenericAliasedAbility
		implements GenericSingleIconActiveAbility {
	public AbstractGenericSingleIconActiveAbility(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		final int autoCastOnOrderId = getAutoCastOnOrderId();
		final int autoCastOffOrderId = getAutoCastOffOrderId();
		if (orderId != 0) {
			if (orderId == autoCastOnOrderId) {
				setAutoCastOn(caster, true);
				return false;
			}
			else if (orderId == autoCastOffOrderId) {
				setAutoCastOn(caster, false);
				return false;
			}
			else {
				return super.checkBeforeQueue(game, caster, orderId, target);
			}
		}
		else {
			return super.checkBeforeQueue(game, caster, orderId, target);
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == getBaseOrderId()) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else if (orderId == OrderIds.smart) {
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	protected abstract void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	protected abstract void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (orderId == getBaseOrderId()) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else if (orderId == OrderIds.smart) {
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	protected abstract void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	protected abstract void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId,
			AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.targetOk(null);
		}
		else if (orderId == getBaseOrderId()) {
			innerCheckCanTargetNoTarget(game, unit, orderId, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	protected abstract void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver);

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public int getUIGoldCost() {
		return 0;
	}

	@Override
	public int getUILumberCost() {
		return 0;
	}

	@Override
	public int getUIFoodCost() {
		return 0;
	}

	@Override
	public int getUIManaCost() {
		return 0;
	}

	@Override
	public int getUsesRemaining() {
		return -1;
	}

	@Override
	public float getUIAreaOfEffect() {
		return Float.NaN;
	}

	@Override
	public boolean isAutoCastOn() {
		return false;
	}

	@Override
	public int getAutoCastOnOrderId() {
		return 0;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return 0;
	}

	public void setAutoCastOn(final CUnit unit, final boolean autoCastOn) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

}
