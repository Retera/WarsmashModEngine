package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.cargohold;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorDrop extends CAbstractRangedBehavior {
	private final CAbilityDrop ability;
	private int lastDropTick = 0;

	public CBehaviorDrop(final CUnit unit, final CAbilityDrop ability) {
		super(unit);
		this.ability = ability;
	}

	public CBehavior reset(CSimulation game, final AbilityPointTarget target) {
		return innerReset(game, target, false);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		final float castRange = this.ability.getCastRange();
		return this.unit.canReach(this.target, castRange);
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final int gameTurnTick = simulation.getGameTurnTick();
		final int deltaTicks = gameTurnTick - this.lastDropTick;
		final CAbilityCargoHold cargoData = this.unit.getCargoData();
		if (cargoData.isEmpty()) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		// TODO i do a nonstandard Math.ceil() here to make this one feel a bit slower
		final float durationTicks = (int) Math.ceil(cargoData.getDuration() / WarsmashConstants.SIMULATION_STEP_TIME);
		if (deltaTicks >= durationTicks) {
			cargoData.dropUnitByIndex(simulation, unit, 0);
			this.unit.notifyOrdersChanged();
			this.lastDropTick = gameTurnTick;
		}
		return this;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.unloadall;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}
}
