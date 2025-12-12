package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.neutral;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorStop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityWander extends AbstractGenericNoIconAbility implements CAbilitySpell {
	private static final War3ID CODE = War3ID.fromString("Awan");
	private static final float AREA_LAND = 200;
	private static final float AREA_AIR = 500;
	private int lastWanderEnd = -1;

	public CAbilityWander(final int handleId, final War3ID alias) {
		super(handleId, CODE, alias);
	}

	@Override
	public void populate(final GameObject worldEditorAbility, final int level) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (!unit.isMovementDisabled()) {
			if ((unit.getCurrentBehavior() instanceof CBehaviorStop) || (unit.getCurrentBehavior() == null)) {
				if (this.lastWanderEnd == -1) {
					this.lastWanderEnd = game.getGameTurnTick();
				}
				final boolean landUnit = unit.getMovementType() != MovementType.FOOT;
				final float range = landUnit ? AREA_LAND : AREA_AIR;
				if (landUnit || ((game.getGameTurnTick() - this.lastWanderEnd) > (5.0f
						/ WarsmashConstants.SIMULATION_STEP_TIME))) {
					this.lastWanderEnd = -1;

					final float randomAngle = game.getSeededRandom().nextFloat() * (float) Math.PI * 2.f;
					unit.order(game, OrderIds.smart,
							new AbilityPointTarget(unit.getX() + (float) (Math.cos(randomAngle) * range),
									unit.getY() + (float) (Math.sin(randomAngle) * range)));
				}
			}
		}
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.MOVEMENT;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final boolean autoOrder, final AbilityPointTarget target,
			final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();

	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int playerIndex,
			final int orderId, final boolean autoOrder, final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();

	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();

	}

}
