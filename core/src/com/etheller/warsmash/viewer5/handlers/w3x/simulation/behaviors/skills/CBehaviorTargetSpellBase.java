package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class CBehaviorTargetSpellBase extends CAbstractRangedBehavior {
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;
	private int castStartTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = true;
	protected final CAbilitySpellBase ability;

	public CBehaviorTargetSpellBase(final CUnit unit, final CAbilitySpellBase ability) {
		super(unit);
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	public CBehaviorTargetSpellBase reset(final CWidget target) {
		innerReset(target, false);
		this.castStartTick = 0;
		this.doneEffect = false;
		this.channeling = true;
		return this;
	}

	public CBehaviorTargetSpellBase reset(final AbilityPointTarget target) {
		innerReset(target, false);
		this.castStartTick = 0;
		this.doneEffect = false;
		this.channeling = true;
		return this;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		final float castRange = this.ability.getCastRange();
		return this.unit.canReach(this.target, castRange);
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.unit.getUnitAnimationListener().playAnimation(false, null, ability.getCastingSecondaryTags(), 1.0f, true);
		if (this.castStartTick == 0) {
			this.castStartTick = simulation.getGameTurnTick();
		}
		final int ticksSinceCast = simulation.getGameTurnTick() - this.castStartTick;
		final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
			if (!this.doneEffect) {
				this.doneEffect = true;
				if (!this.unit.chargeMana(this.ability.getManaCost())) {
					simulation.getCommandErrorListener().showNoManaError(this.unit.getPlayerIndex());
					return this.unit.pollNextOrderBehavior(simulation);
				}
				this.ability.setCooldownRemaining(this.ability.getCooldown());
				this.unit.fireCooldownsChangedEvent();
			}
			channeling = channeling && ability.doEffect(simulation, unit, target);
		}
		if ((ticksSinceCast >= backswingTicks) && !channeling) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		return this;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(this.stillAliveVisitor.reset(simulation, this.unit, this.ability.getTargetsAllowed()));
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
		return ability.getBaseOrderId();
	}

	public CAbilitySpellBase getAbility() {
		return ability;
	}
}
