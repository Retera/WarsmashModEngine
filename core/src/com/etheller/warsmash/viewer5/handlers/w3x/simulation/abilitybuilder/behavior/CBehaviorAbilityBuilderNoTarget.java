package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CBehaviorAbilityBuilderNoTarget implements ABBehavior {
	private final AbilityBuilderConfiguration parser;
	private Map<String, Object> localStore;
	private AbilityBuilderAbility ability;
	
	private int castStartTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = false;
	
	private int castId = 0;
	private CUnit unit;
	
	private boolean instant = false;

	public CBehaviorAbilityBuilderNoTarget(final CUnit unit, final AbilityBuilderConfiguration parser,
			final Map<String, Object> localStore, AbilityBuilderAbility ability) {
		this.unit = unit;
		this.parser = parser;
		this.localStore = localStore;
		this.ability = ability;
	}
	
	public void setInstant(boolean instant) {
		this.instant = instant;
	}

	@Override
	public ABBehavior reset(final CWidget target) {
		return null;
	}

	@Override
	public ABBehavior reset(CWidget target, int orderId) {
		return null;
	}

	@Override
	public ABBehavior reset(final AbilityPointTarget target) {
		return null;
	}

	@Override
	public ABBehavior reset(AbilityPointTarget target, int orderId) {
		return null;
	}

	@Override
	public ABBehavior reset() {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		return this;
	}

	@Override
	public ABBehavior reset(int orderId) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		boolean wasChanneling = this.channeling;
		if (!instant) {
			this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
					this.ability.getCastingSecondaryTags(), 1.0f, true);
		}
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();
			
			if (!this.unit.chargeMana(this.ability.getUIManaCost())) {
				game.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(),
						CommandStringErrorKeys.NOT_ENOUGH_MANA);
				return this.unit.pollNextOrderBehavior(game);
			}
			this.ability.startCooldown(game, this.unit);
			
			if (parser.getOnBeginCasting() != null) {
				for (ABAction action : parser.getOnBeginCasting()) {
					action.runAction(game, this.unit, localStore, castId);
				}
			}
			this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
		}
		
		if (instant) {
			tryDoEffect(game, wasChanneling);
			if (!this.channeling) {
				return this.unit.pollNextOrderBehavior(game);
			}
		} else {
			final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
			final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
				tryDoEffect(game, wasChanneling);
			}
			if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
				return this.unit.pollNextOrderBehavior(game);
			}
		}
		return this;
	}
	
	private void tryDoEffect(CSimulation game, boolean wasChanneling) {
		boolean wasEffectDone = this.doneEffect;
		if (!wasEffectDone) {
			this.doneEffect = true;
			if (this.channeling) {
				game.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			else {
				game.unitSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			
			if (parser.getOnEndCasting() != null) {
				for (ABAction action : parser.getOnEndCasting()) {
					action.runAction(game, this.unit, localStore, castId);
				}
			}
			this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
			
		}
		this.channeling = this.channeling && this.doChannelTick(game, this.unit);
		if (wasEffectDone && wasChanneling && !this.channeling) {
			endChannel(game, false);
		}
	}

	@Override
	public void begin(final CSimulation game) {
	}

	public boolean doChannelTick(CSimulation game, CUnit caster) {
		if (parser.getOnChannelTick() != null) {
			for (ABAction action : parser.getOnChannelTick()) {
				action.runAction(game, this.unit, localStore, castId);
			}
		}
		return (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		checkEndChannel(game, interrupted);
	}

	private void checkEndChannel(final CSimulation game, final boolean interrupted) {
		if (this.channeling) {
			this.channeling = false;
			this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
			endChannel(game, interrupted);
		}
	}

	private void endChannel(CSimulation game, boolean interrupted) {
		this.localStore.put(ABLocalStoreKeys.INTERRUPTED, interrupted);
		game.unitStopSoundEffectEvent(this.unit, this.ability.getAlias());
		if (parser.getOnEndChannel() != null) {
			for (ABAction action : parser.getOnEndChannel()) {
				action.runAction(game, this.unit, localStore, castId);
			}
		}
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	public void setCastId(int castId) {
		this.castId = castId;
	}

}
