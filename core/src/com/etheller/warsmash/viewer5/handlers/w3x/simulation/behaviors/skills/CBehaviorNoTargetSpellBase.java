package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

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
				this.channeling = this.ability.doEffect(simulation, this.unit, null);
				if (this.channeling) {
					simulation.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
				} else {
					simulation.unitSoundEffectEvent(this.unit, this.ability.getAlias());
				}
			}
			this.channeling = this.channeling && this.ability.doChannelTick(simulation, this.unit, null);
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
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	public CAbilitySpellBase getAbility() {
		return this.ability;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}
}
