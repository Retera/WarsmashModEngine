package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.StringsToExternalizeLater;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CAttackOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CMoveOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;

public class CAbilityAttack implements CAbility {
	private final int handleId;

	public CAbilityAttack(final int handleId) {
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
		if ((orderId == OrderIds.smart) || (orderId == OrderIds.attack)) {
			boolean canTarget = false;
			for (final CUnitAttack attack : unit.getUnitType().getAttacks()) {
				if (target.canBeTargetedBy(game, unit, attack.getTargetsAllowed())) {
					canTarget = true;
					break;
				}
			}
			if (canTarget) {
				receiver.targetOk(target);
			}
			else {
				// TODO obviously we should later support better warnings here
				receiver.mustTargetType(TargetType.UNIT);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		receiver.mustTargetType(TargetType.UNIT);
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.mustTargetType(TargetType.UNIT);
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
		COrder order = null;
		for (final CUnitAttack attack : caster.getUnitType().getAttacks()) {
			if (target.canBeTargetedBy(game, caster, attack.getTargetsAllowed())) {
				order = new CAttackOrder(caster, attack, orderId, target);
				break;
			}
		}
		if (order == null) {
			order = new CMoveOrder(caster, orderId, target.getX(), target.getY());
		}
		caster.order(order, queue);
	}

	@Override
	public void onOrder(final CSimulation game, final CUnit caster, final int orderId, final Vector2 target,
			final boolean queue) {
		throw new IllegalArgumentException(StringsToExternalizeLater.MUST_TARGET_WIDGET);
	}

	@Override
	public void onOrderNoTarget(final CSimulation game, final CUnit caster, final int orderId, final boolean queue) {
		throw new IllegalArgumentException(StringsToExternalizeLater.MUST_TARGET_WIDGET);
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
