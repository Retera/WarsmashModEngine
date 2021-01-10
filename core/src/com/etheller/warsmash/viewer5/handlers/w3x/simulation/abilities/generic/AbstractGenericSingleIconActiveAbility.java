package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public abstract class AbstractGenericSingleIconActiveAbility extends AbstractCAbility
		implements GenericSingleIconActiveAbility {
	private final War3ID alias;

	public AbstractGenericSingleIconActiveAbility(final int handleId, final War3ID alias) {
		super(handleId);
		this.alias = alias;
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId) {
		return true;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == getBaseOrderId()) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	protected abstract void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (orderId == getBaseOrderId()) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	protected abstract void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if (orderId == getBaseOrderId()) {
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
	public War3ID getAlias() {
		return this.alias;
	}

}
