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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

import java.net.CookieManager;

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
		this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
				this.ability.getCastingSecondaryTags(), 1.0f, true);
		if (this.castStartTick == 0) {
			this.castStartTick = simulation.getGameTurnTick();
		}
		final int ticksSinceCast = simulation.getGameTurnTick() - this.castStartTick;
		final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
			boolean wasEffectDone = this.doneEffect;
			boolean wasChanneling = this.channeling;
			if (!wasEffectDone) {
				this.doneEffect = true;
				if (!this.unit.chargeMana(this.ability.getManaCost())) {
					simulation.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(), CommandStringErrorKeys.NOT_ENOUGH_MANA);
					return this.unit.pollNextOrderBehavior(simulation);
				}
				this.unit.beginCooldown(simulation, this.ability.getCode(), this.ability.getCooldown());
				this.channeling = this.ability.doEffect(simulation, this.unit, this.target);
				if (this.channeling) {
					simulation.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
				} else {
					simulation.unitSoundEffectEvent(this.unit, this.ability.getAlias());
				}
			}
			this.channeling = this.channeling && this.ability.doChannelTick(simulation, this.unit, this.target);
			if (wasEffectDone && wasChanneling && !this.channeling) {
				simulation.unitStopSoundEffectEvent(this.unit, this.ability.getAlias());
			}
		}
		if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
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
		/*
		 * BELOW: "doneEffect" allows us to channel "at" something that died, if you hit
		 * a bug with that, then fix it here
		 */
		return this.doneEffect || this.target
				.visit(this.stillAliveVisitor.reset(simulation, this.unit, this.ability.getTargetsAllowed()));
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
		return this.ability.getBaseOrderId();
	}

	public CAbilitySpellBase getAbility() {
		return this.ability;
	}
}
