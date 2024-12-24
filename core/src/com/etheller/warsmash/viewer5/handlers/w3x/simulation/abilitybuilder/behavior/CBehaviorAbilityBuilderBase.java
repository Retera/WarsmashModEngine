package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CBehaviorAbilityBuilderBase extends CAbstractRangedBehavior implements ABBehavior {
	private Map<String, Object> localStore;
	private AbilityBuilderActiveAbility ability;
	private ABAbilityTargetStillTargetableVisitor preCastTargetableVisitor;
	
	private int castStartTick = 0;
	private int castBehaviorNotifyTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = false;
	private boolean preventReInterrupt = false;
	
	private int castId = 0;
	private int orderId;
	
	private boolean instant = false;
	private CBehaviorCategory behaviorCategory = null;

	public CBehaviorAbilityBuilderBase(final CUnit unit,
			final Map<String, Object> localStore, AbilityBuilderActiveAbility ability) {
		super(unit);
		this.localStore = localStore;
		this.ability = ability;
		this.preCastTargetableVisitor = new ABAbilityTargetStillTargetableVisitor();
		
	}
	
	public void setInstant(boolean instant) {
		this.instant = instant;
	}
	
	public void setBehaviorCategory(CBehaviorCategory behaviorCategory) {
		this.behaviorCategory = behaviorCategory;
	}

	public CBehavior reset(final CSimulation game, final CWidget target) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	public CBehavior reset(final CSimulation game, final CWidget target, int orderId) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	public CBehavior reset(final CSimulation game, final AbilityPointTarget target) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	public CBehavior reset(final CSimulation game, final AbilityPointTarget target, int orderId) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	public ABBehavior reset() {
		return null;
	}

	public ABBehavior reset(int orderId) {
		return null;
	}

	@Override
	public CBehavior update(final CSimulation game, boolean withinFacingWindow) {
		boolean wasChanneling = this.channeling;
		if (this.castStartTick == 0) {
			CBehavior prevBeh = this.unit.getCurrentBehavior();
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, this.target);
			if (this.unit.getCurrentBehavior() != prevBeh) {
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}
			
			this.castStartTick = game.getGameTurnTick();
			this.castBehaviorNotifyTick = (int) (this.castStartTick + 0.5/WarsmashConstants.SIMULATION_STEP_TIME);
			
			if (!this.target.visit(this.preCastTargetableVisitor.reset(game, this.unit, ability, false, orderId))) {
				return this.unit.pollNextOrderBehavior(game);
			}
			
			if (!this.unit.chargeMana(this.ability.getChargedManaCost())) {
				game.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(),
						CommandStringErrorKeys.NOT_ENOUGH_MANA);
				return this.unit.pollNextOrderBehavior(game);
			}
			this.ability.startCooldown(game, this.unit);
			
			this.ability.runBeginCastingActions(game, unit, orderId);

			CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (newBehavior != null) {
				cleanupInputs();
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
				cleanupInputs();
				localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				return newBehavior;
			}
			
			if (!this.channeling) {
				cleanupInputs();
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
					cleanupInputs();
					localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
					return newBehavior;
				}
			}
			if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
				cleanupInputs();
				return this.unit.pollNextOrderBehavior(game);
			}
		}
		return this;
	}
	
	private void cleanupInputs() {
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + castId);
	}
	
	private CBehavior tryDoEffect(CSimulation game, boolean wasChanneling) {
		boolean wasEffectDone = this.doneEffect;
		if (!wasEffectDone) {
			CBehavior prevBeh = this.unit.getCurrentBehavior();
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this.ability, this.target);
			if (this.unit.getCurrentBehavior() != prevBeh) {
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}
			if (this.channeling) {
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.ability, this.target);
				if (this.unit.getCurrentBehavior() != prevBeh) {
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
		this.channeling = this.channeling && this.doChannelTick(game, this.unit, this.target);
		if (wasEffectDone && wasChanneling && !this.channeling) {
			endChannel(game, false);
		}
		return null;
	}

	@Override
	public void begin(final CSimulation game) {
	}

	public boolean doChannelTick(CSimulation game, CUnit caster, AbilityTarget target) {
		this.ability.runChannelTickActions(game, caster, orderId);
		return (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		checkEndChannel(game, interrupted);
		this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.ability, this.target);
		this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.ability, this.target);
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
		cleanupInputs();
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	@Override
	public boolean isWithinRange(CSimulation simulation) {
		return this.unit.canReach(this.target, this.ability.getCastRange());
	}

	@Override
	public void endMove(CSimulation game, boolean interrupted) {
		if (interrupted && !preventReInterrupt) {
			preventReInterrupt = true;
			this.ability.runCancelPreCastActions(game, unit, orderId);
			checkEndChannel(game, interrupted);
		}
	}

	@Override
	protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(CSimulation simulation) {
		return this.doneEffect || this.target.visit(this.preCastTargetableVisitor.reset(simulation, this.unit, this.ability, this.channeling, orderId));
	}

	@Override
	protected void resetBeforeMoving(CSimulation simulation) {
		this.castStartTick = 0;
	}
	
	public void setCastId(int castId) {
		this.castId = castId;
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

}
