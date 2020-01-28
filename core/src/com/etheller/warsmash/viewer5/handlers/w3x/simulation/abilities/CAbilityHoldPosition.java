package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CDoNothingOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;

public class CAbilityHoldPosition implements CAbility {
	public static final int ORDER_ID = 860002; // fake, later will use WC3 one probably
	public static CAbilityHoldPosition INSTANCE = new CAbilityHoldPosition();

	@Override
	public void checkCanUse(final CSimulation game, final CUnit unit, final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.mustTargetType(TargetType.NO_TARGET);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		receiver.mustTargetType(TargetType.NO_TARGET);
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.targetOk(null);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onOrder(final CSimulation game, final CUnit caster, final CWidget target, final boolean queue) {
		caster.order(new CDoNothingOrder(getOrderId()), queue);
	}

	@Override
	public void onOrder(final CSimulation game, final CUnit caster, final Vector2 target, final boolean queue) {
		caster.order(new CDoNothingOrder(getOrderId()), queue);
	}

	@Override
	public void onOrderNoTarget(final CSimulation game, final CUnit caster, final boolean queue) {
		caster.order(new CDoNothingOrder(getOrderId()), queue);
	}

	@Override
	public int getOrderId() {
		return ORDER_ID;
	}

}
