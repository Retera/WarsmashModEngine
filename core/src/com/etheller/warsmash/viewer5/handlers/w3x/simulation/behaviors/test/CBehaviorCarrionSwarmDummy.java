package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.test;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorCarrionSwarmDummy extends CAbstractRangedBehavior {

	private final CAbilityCarrionSwarmDummy abilityCarrionSwarmDummy;
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveAndTargetableVisitor = new AbilityTargetStillAliveAndTargetableVisitor();

	public CBehaviorCarrionSwarmDummy(final CUnit unit, final CAbilityCarrionSwarmDummy abilityCarrionSwarmDummy) {
		super(unit);
		this.abilityCarrionSwarmDummy = abilityCarrionSwarmDummy;
	}

	public CBehavior reset(CSimulation game, final AbilityTarget target) {
		return innerReset(game, target);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.abilityCarrionSwarmDummy.getCastRange());
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
		return OrderIds.carrionswarm;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final int playerIndex = this.unit.getPlayerIndex();
		if (this.target instanceof AbilityPointTarget) {
			simulation.getPlayer(playerIndex).fireAbilityEffectEventsPoint(this.abilityCarrionSwarmDummy, this.unit,
					((AbilityPointTarget) this.target), this.abilityCarrionSwarmDummy.getAlias());
		}
		else if (this.target instanceof CUnit) {
			simulation.getPlayer(playerIndex).fireAbilityEffectEventsTarget(this.abilityCarrionSwarmDummy, this.unit,
					((CUnit) this.target), this.abilityCarrionSwarmDummy.getAlias());
		} // TODO other kinds of widgets
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(this.stillAliveAndTargetableVisitor.reset(simulation, this.unit,
				this.abilityCarrionSwarmDummy.getTargetsAllowed()));
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

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
