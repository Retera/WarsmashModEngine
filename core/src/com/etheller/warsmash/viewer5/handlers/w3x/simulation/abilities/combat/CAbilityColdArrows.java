package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

/**
 * Represents an ability from the object data
 */
public class CAbilityColdArrows implements CAbility {
	private final War3ID rawcode;
	private final int handleId;
	private boolean autoCastActive;

	public CAbilityColdArrows(final War3ID rawcode, final int handleId) {
		this.rawcode = rawcode;
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
		case OrderIds.coldarrowstarg:
			receiver.targetOk(target);
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		switch (orderId) {
		case OrderIds.coldarrows:
		case OrderIds.uncoldarrows:
			receiver.targetOk(null);
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	public War3ID getRawcode() {
		return this.rawcode;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	public boolean isAutoCastActive() {
		return this.autoCastActive;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		CBehavior behavior = null;
		for (final CUnitAttack attack : caster.getUnitType().getAttacks()) {
			if (target.canBeTargetedBy(game, caster, attack.getTargetsAllowed())) {
				behavior = caster.getAttackBehavior().reset(OrderIds.coldarrowstarg, attack, target);
				break;
			}
		}
		if (behavior != null) {
			return behavior;
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final Vector2 point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		this.autoCastActive = !this.autoCastActive;
		return caster.pollNextOrderBehavior(game);
	}
}
