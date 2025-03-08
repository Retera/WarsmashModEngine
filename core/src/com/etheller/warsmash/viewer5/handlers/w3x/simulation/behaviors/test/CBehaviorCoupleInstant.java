package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.test;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityCoupleInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorCoupleInstant extends CAbstractRangedBehavior {

	private final CAbilityCoupleInstant abilityCoupleInstant;
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;

	public CBehaviorCoupleInstant(final CUnit unit, final CAbilityCoupleInstant abilityCoupleInstant) {
		super(unit);
		this.abilityCoupleInstant = abilityCoupleInstant;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	public CBehavior reset(CSimulation game, final CUnit coupleTarget) {
		return innerReset(game, coupleTarget);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.abilityCoupleInstant.getCastRange());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.coupleinstant;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinRange) {
		final CBehavior targetBehavior = ((CUnit) this.target).getCurrentBehavior();
		if (targetBehavior instanceof CBehaviorCoupleInstant) {
			if (((CBehaviorCoupleInstant) targetBehavior).isWithinRange(simulation)) {
				// we are both within range
				final int goldCost = this.abilityCoupleInstant.getGoldCost();
				final int lumberCost = this.abilityCoupleInstant.getLumberCost();
				if (((goldCost == 0) && (lumberCost == 0))
						|| simulation.getPlayer(this.unit.getPlayerIndex()).charge(goldCost, lumberCost)) {
					final CUnit newUnit = simulation.createUnit(this.abilityCoupleInstant.getResultingUnitType(),
							this.unit.getPlayerIndex(), this.unit.getX(), this.unit.getY(), this.unit.getFacing());
					simulation.unitPreferredSelectionReplacement(this.unit, newUnit);
					simulation.unitPreferredSelectionReplacement(((CUnit) this.target), newUnit);
					simulation.removeUnit(this.unit);
					simulation.removeUnit((CUnit) this.target);
					simulation.unitSoundEffectEvent(newUnit, this.abilityCoupleInstant.getAlias());
				}
				return this.unit.pollNextOrderBehavior(simulation);
			}
		}
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
		return this;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(
				this.stillAliveVisitor.reset(simulation, this.unit, this.abilityCoupleInstant.getTargetsAllowed()));
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public AbilityTarget getTarget() {
		return this.target;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}

}
