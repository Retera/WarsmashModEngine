package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CBehaviorAbilityBuilderNoTarget implements ABBehavior {
	private Map<String, Object> localStore;
	private AbilityBuilderActiveAbility ability;
	
	private int castStartTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = false;
	
	private CUnit unit;
	private int orderId;
	
	private boolean instant = false;
	private CBehaviorCategory behaviorCategory = null;

	public CBehaviorAbilityBuilderNoTarget(final CUnit unit,
			final Map<String, Object> localStore, AbilityBuilderActiveAbility ability) {
		this.unit = unit;
		this.localStore = localStore;
		this.ability = ability;
	}
	
	public void setInstant(boolean instant) {
		this.instant = instant;
	}
	
	public void setBehaviorCategory(CBehaviorCategory behaviorCategory) {
		this.behaviorCategory  = behaviorCategory;
	}

	@Override
	public ABBehavior reset(final CSimulation game, final CWidget target) {
		return null;
	}

	@Override
	public ABBehavior reset(final CSimulation game, CWidget target, int orderId) {
		return null;
	}

	@Override
	public ABBehavior reset(final CSimulation game, final AbilityPointTarget target) {
		return null;
	}

	@Override
	public ABBehavior reset(final CSimulation game, AbilityPointTarget target, int orderId) {
		return null;
	}

	@Override
	public ABBehavior reset() {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		return this;
	}

	@Override
	public ABBehavior reset(int orderId) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		boolean wasChanneling = this.channeling;
		if (this.castStartTick == 0) {
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, null);
			if (this.unit.getCurrentBehavior() != this) {
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}
			this.castStartTick = game.getGameTurnTick();
			
			if (!this.unit.chargeMana(this.ability.getChargedManaCost())) {
				game.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(),
						CommandStringErrorKeys.NOT_ENOUGH_MANA);
				return this.unit.pollNextOrderBehavior(game);
			}
			this.ability.startCooldown(game, this.unit);
			
			this.ability.runBeginCastingActions(game, unit, orderId);
			CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (newBehavior != null) {
				localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				return newBehavior;
			}
			if (!instant) {
				this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
						this.ability.getCastingSecondaryTags(), 1.0f, true);
			}
			this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
		}
		
		if (instant) {
			CBehavior beh = tryDoEffect(game, wasChanneling);
			if (beh != null) {
				return beh;
			}
			CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (newBehavior != null) {
				localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				return newBehavior;
			}
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
				CBehavior beh = tryDoEffect(game, wasChanneling);
				if (beh != null) {
					return beh;
				}
				CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
				if (newBehavior != null) {
					localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
					return newBehavior;
				}
			}
			if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
				return this.unit.pollNextOrderBehavior(game);
			}
		}
		return this;
	}
	
	private CBehavior tryDoEffect(CSimulation game, boolean wasChanneling) {
		boolean wasEffectDone = this.doneEffect;
		if (!wasEffectDone) {
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this.ability, null);
			if (this.unit.getCurrentBehavior() != this) {
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}
			if (this.channeling) {
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.ability, null);
				if (this.unit.getCurrentBehavior() != this) {
					return this.unit.getCurrentBehavior();
				} else if (this.unit.isPaused()) {
					return this;
				}
				game.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			else {
				game.unitSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			this.doneEffect = true;
			
			this.ability.runEndCastingActions(game, unit, orderId);
			this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
			
		}
		this.channeling = this.channeling && this.doChannelTick(game, this.unit);
		if (wasEffectDone && wasChanneling && !this.channeling) {
			endChannel(game, false);
		}
		return null;
	}

	@Override
	public void begin(final CSimulation game) {
	}

	public boolean doChannelTick(CSimulation game, CUnit caster) {
		this.ability.runChannelTickActions(game, caster, orderId);
		return (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		checkEndChannel(game, interrupted);
		this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.ability, null);
		this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.ability, null);
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
		this.ability.runEndChannelActions(game, unit, orderId);
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	public void setCastId(int castId) {
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		if (this.behaviorCategory != null) {
			return this.behaviorCategory ;
		}
		return CBehaviorCategory.SPELL;
	}

	@Override
	public CAbility getAbility() {
		return ability;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
