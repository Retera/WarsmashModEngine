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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;

public class CAbilityAttack implements CAbility {
	public static final int ORDER_ID = 860000; // fake, later will use WC3 one probably
	public static final CAbilityAttack INSTANCE = new CAbilityAttack();

	@Override
	public void checkCanUse(final CSimulation game, final CUnit unit, final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.targetOk(target);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		receiver.mustTargetType(TargetType.UNIT);
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit,
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
	public void onOrder(final CSimulation game, final CUnit caster, final CWidget target, final boolean queue) {
		COrder order = null;
		for (final CUnitAttack attack : caster.getUnitType().getAttacks()) {
			if (target.canBeTargetedBy(game, caster, attack.getTargetsAllowed())) {
				order = new CAttackOrder(caster, attack, target);
				break;
			}
		}
		if (order == null) {
			order = new CMoveOrder(caster, target.getX(), target.getY());
		}
		caster.order(order, queue);
	}

	@Override
	public void onOrder(final CSimulation game, final CUnit caster, final Vector2 target, final boolean queue) {
		throw new IllegalArgumentException(StringsToExternalizeLater.MUST_TARGET_WIDGET);
	}

	@Override
	public void onOrderNoTarget(final CSimulation game, final CUnit caster, final boolean queue) {
		throw new IllegalArgumentException(StringsToExternalizeLater.MUST_TARGET_WIDGET);
	}

	@Override
	public int getOrderId() {
		return ORDER_ID;
	}

}
