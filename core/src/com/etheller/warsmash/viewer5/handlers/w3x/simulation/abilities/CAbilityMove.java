package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;

public class CAbilityMove extends AbstractCAbility {

	public CAbilityMove(final int handleId) {
		super(handleId);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		switch (orderId) {
		case OrderIds.smart:
		case OrderIds.patrol:
		case OrderIds.move:
			if ((target instanceof CUnit) && (target != unit)) {
				receiver.targetOk(target);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
			}
			break;
		default:
			receiver.orderIdNotAccepted();
			break;
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
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
		unit.setMoveBehavior(new CBehaviorMove(unit));
		unit.setFollowBehavior(new CBehaviorFollow(unit));
		unit.setPatrolBehavior(new CBehaviorPatrol(unit));
		unit.setHoldPositionBehavior(new CBehaviorHoldPosition(unit));
		unit.setBoardTransportBehavior(new CBehaviorBoardTransport(unit));
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		boolean smart = orderId == OrderIds.smart;
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			CBehavior behavior = null;
			if (smart) {
				CAbilityLoad transportLoad = CAbilityLoad.getTransportLoad(game, caster, targetUnit, true, true);
				if (transportLoad != null) {
					behavior = caster.getBoardTransportBehavior().reset(OrderIds.move, targetUnit);
				}
			}
			if (behavior == null) {
				behavior = caster.getFollowBehavior()
						.reset(smart ? OrderIds.move : orderId, targetUnit);
				caster.setDefaultBehavior(behavior);
			}
			return behavior;
		}
		// NOTE: shouldn't happen, target is always unit for this ability
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		if (orderId == OrderIds.patrol) {
			final CBehavior patrolBehavior = caster.getPatrolBehavior().reset(point);
			caster.setDefaultBehavior(patrolBehavior);
			return patrolBehavior;
		}
		else {
			return caster.getMoveBehavior().reset(OrderIds.move, point);
		}
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if (orderId == OrderIds.holdposition) {
			caster.setDefaultBehavior(caster.getHoldPositionBehavior());
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

}
