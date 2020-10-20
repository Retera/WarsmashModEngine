package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.StringsToExternalizeLater;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CMoveOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CPatrolOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;

public class CAbilityMove implements CAbility {
	private final int handleId;

	public CAbilityMove(final int handleId) {
		this.handleId = handleId;
	}

	@Override
	public void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		switch (orderId) {
		case OrderIds.smart:
		case OrderIds.patrol:
			if (target instanceof CUnit) {
				receiver.targetOk(target);
			}
			else {
				receiver.mustTargetType(TargetType.UNIT_OR_POINT);
			}
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		switch (orderId) {
		case OrderIds.smart:
		case OrderIds.move:
		case OrderIds.patrol:
			receiver.targetOk(target);
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		switch (orderId) {
		case OrderIds.holdposition:
			receiver.targetOk(null);
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onOrder(final CSimulation game, final CUnit caster, final int orderId, final CWidget target,
			final boolean queue) {
		caster.order(new CPatrolOrder(caster, orderId, (CUnit) target), queue);
	}

	@Override
	public void onOrder(final CSimulation game, final CUnit caster, final int orderId, final Vector2 target,
			final boolean queue) {
		caster.order(new CMoveOrder(caster, orderId, target.x, target.y), queue);
	}

	@Override
	public void onOrderNoTarget(final CSimulation game, final CUnit caster, final int orderId, final boolean queue) {
		throw new IllegalArgumentException(StringsToExternalizeLater.MUST_TARGET_POINT);
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}
