package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class CBehaviorNoTargetSpellBase implements CBehavior {
	protected final CUnit unit;
	protected final CAbilitySpellBase ability;
	private int castStartTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = true;

	public CBehaviorNoTargetSpellBase(final CUnit unit, final CAbilitySpellBase ability) {
		this.unit = unit;
		this.ability = ability;
	}

	public CBehaviorNoTargetSpellBase reset() {
		this.castStartTick = 0;
		this.doneEffect = false;
		this.channeling = true;
		return this;
	}

	@Override
	public CBehavior update(final CSimulation simulation) {
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
			channeling = channeling && ability.doEffect(simulation, unit, null);
		}
		if ((ticksSinceCast >= backswingTicks) && !channeling) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return ability.getBaseOrderId();
	}

	public CAbilitySpellBase getAbility() {
		return ability;
	}
}
