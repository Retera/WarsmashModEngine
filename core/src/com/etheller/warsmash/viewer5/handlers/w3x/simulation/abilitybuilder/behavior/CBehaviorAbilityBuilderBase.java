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
	private final Map<String, Object> localStore;
	private final AbilityBuilderActiveAbility ability;
	private final ABAbilityTargetStillTargetableVisitor preCastTargetableVisitor;

	private int castStartTick = 0;
	private int castBehaviorNotifyTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = false;
	private boolean preventReInterrupt = false;

	private int castId = 0;
	private int orderId;

	private boolean instant = false;
	private CBehaviorCategory behaviorCategory = null;

	public CBehaviorAbilityBuilderBase(final CUnit unit, final Map<String, Object> localStore,
			final AbilityBuilderActiveAbility ability) {
		super(unit);
		this.localStore = localStore;
		this.ability = ability;
		this.preCastTargetableVisitor = new ABAbilityTargetStillTargetableVisitor();

	}

	@Override
	public void setInstant(final boolean instant) {
		this.instant = instant;
	}

	@Override
	public void setBehaviorCategory(final CBehaviorCategory behaviorCategory) {
		this.behaviorCategory = behaviorCategory;
	}

	@Override
	public CBehavior reset(final CSimulation game, final CWidget target) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	@Override
	public CBehavior reset(final CSimulation game, final CWidget target, final int orderId) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	@Override
	public CBehavior reset(final CSimulation game, final AbilityPointTarget target) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	@Override
	public CBehavior reset(final CSimulation game, final AbilityPointTarget target, final int orderId) {
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		this.preventReInterrupt = false;
		return innerReset(game, target, false);
	}

	@Override
	public ABBehavior reset() {
		return null;
	}

	@Override
	public ABBehavior reset(final int orderId) {
		return null;
	}

	@Override
	public CBehavior update(final CSimulation game, final boolean withinFacingWindow) {
		final boolean wasChanneling = this.channeling;
		if (this.castStartTick == 0) {
			final CBehavior prevBeh = this.unit.getCurrentBehavior();
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, this.target);
			if (this.unit.getCurrentBehavior() != prevBeh) {
				return this.unit.getCurrentBehavior();
			}
			else if (this.unit.isPaused()) {
				return this;
			}

			this.castStartTick = game.getGameTurnTick();
			this.castBehaviorNotifyTick = (int) (this.castStartTick + (0.5 / WarsmashConstants.SIMULATION_STEP_TIME));

			if (!this.target.visit(this.preCastTargetableVisitor.reset(game, this.unit, this.ability, false,
					this.unit.getPlayerIndex(), this.orderId))) {
				return this.unit.pollNextOrderBehavior(game);
			}

			if (!this.unit.chargeMana(this.ability.getChargedManaCost())) {
				game.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(),
						CommandStringErrorKeys.NOT_ENOUGH_MANA);
				return this.unit.pollNextOrderBehavior(game);
			}
			this.ability.startCooldown(game, this.unit);

			this.ability.runBeginCastingActions(game, this.unit, this.orderId);

			final CBehavior newBehavior = (CBehavior) this.localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (newBehavior != null) {
				cleanupInputs();
				this.localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				return newBehavior;
			}
			if (!this.instant) {
				this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
						this.ability.getCastingSecondaryTags(), 1.0f, true);
			}
			this.channeling = (boolean) this.localStore.get(ABLocalStoreKeys.CHANNELING);
		}

		if (this.instant) {
			final CBehavior beh = tryDoEffect(game, wasChanneling);
			if (beh != null) {
				return beh;
			}
			final CBehavior newBehavior = (CBehavior) this.localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (newBehavior != null) {
				cleanupInputs();
				this.localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				return newBehavior;
			}

			if (!this.channeling) {
				cleanupInputs();
				return this.unit.pollNextOrderBehavior(game);
			}
		}
		else {
			final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
			final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
				final CBehavior beh = tryDoEffect(game, wasChanneling);
				if (beh != null) {
					return beh;
				}
				final CBehavior newBehavior = (CBehavior) this.localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
				if (newBehavior != null) {
					cleanupInputs();
					this.localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
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
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + this.castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + this.castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + this.castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId);
	}

	private CBehavior tryDoEffect(final CSimulation game, final boolean wasChanneling) {
		final boolean wasEffectDone = this.doneEffect;
		if (!wasEffectDone) {
			final CBehavior prevBeh = this.unit.getCurrentBehavior();
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this.ability, this.target);
			if (this.unit.getCurrentBehavior() != prevBeh) {
				return this.unit.getCurrentBehavior();
			}
			else if (this.unit.isPaused()) {
				return this;
			}
			if (this.channeling) {
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.ability, this.target);
				if (this.unit.getCurrentBehavior() != prevBeh) {
					return this.unit.getCurrentBehavior();
				}
				else if (this.unit.isPaused()) {
					return this;
				}
				game.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			else {
				game.unitSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			this.doneEffect = true;

			this.ability.runEndCastingActions(game, this.unit, this.orderId);
			this.channeling = (boolean) this.localStore.get(ABLocalStoreKeys.CHANNELING);

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

	public boolean doChannelTick(final CSimulation game, final CUnit caster, final AbilityTarget target) {
		this.ability.runChannelTickActions(game, caster, this.orderId);
		return (boolean) this.localStore.get(ABLocalStoreKeys.CHANNELING);
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
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

	private void endChannel(final CSimulation game, final boolean interrupted) {
		this.localStore.put(ABLocalStoreKeys.INTERRUPTED, interrupted);
		game.unitStopSoundEffectEvent(this.unit, this.ability.getAlias());
		this.ability.runEndChannelActions(game, this.unit, this.orderId);
		cleanupInputs();
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.ability.getCastRange());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
		if (interrupted && !this.preventReInterrupt) {
			this.preventReInterrupt = true;
			this.ability.runCancelPreCastActions(game, this.unit, this.orderId);
			checkEndChannel(game, interrupted);
		}
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.doneEffect || this.target.visit(this.preCastTargetableVisitor.reset(simulation, this.unit,
				this.ability, this.channeling, this.unit.getPlayerIndex(), this.orderId));
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		this.castStartTick = 0;
	}

	@Override
	public void setCastId(final int castId) {
		this.castId = castId;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		if (this.behaviorCategory != null) {
			return this.behaviorCategory;
		}
		return CBehaviorCategory.SPELL;
	}

	@Override
	public CAbility getAbility() {
		return this.ability;
	}

}
