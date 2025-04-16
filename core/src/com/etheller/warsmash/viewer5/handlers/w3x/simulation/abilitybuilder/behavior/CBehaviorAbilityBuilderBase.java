package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.EnumSet;
import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;

public class CBehaviorAbilityBuilderBase extends CAbstractRangedBehavior implements ABBehavior {
	private Map<String, Object> localStore;
	private AbilityBuilderActiveAbility ability;
	private ABAbilityTargetStillTargetableVisitor preCastTargetableVisitor;

	private int castBehaviorNotifyTick = 0;

	private int castStartTick = 0;
	private int castTimeEndTick = 0;
	private int castPointTicks = 0;
	private int backswingTicks = 0;
	private boolean doneCastTime = false;
	private boolean doneEffect = false;
	private boolean channeling = false;
	private boolean preventReInterrupt = false;

	private int castId = 0;
	private int orderId;
	private boolean autoOrder;

	private boolean instant = false;
	private CBehaviorCategory behaviorCategory = null;

	public CBehaviorAbilityBuilderBase(final CUnit unit, final Map<String, Object> localStore,
			AbilityBuilderActiveAbility ability) {
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

	public CBehavior reset(final CSimulation game, final CWidget target, final boolean autoOrder) {
		this.doneCastTime = false;
		this.doneEffect = false;
		this.castStartTick = 0;
		this.castTimeEndTick = this.ability.ignoreCastTime() ? 0
				: (int) (this.ability.getCastTime() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.castPointTicks = this.castTimeEndTick
				+ (int) (this.unit.getUnitType().getCastPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.backswingTicks = this.castPointTicks
				+ (int) (this.unit.getUnitType().getCastBackswingPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		this.preventReInterrupt = false;
		this.autoOrder = autoOrder;
		return innerReset(game, target, false);
	}

	public CBehavior reset(final CSimulation game, final CWidget target, int orderId, final boolean autoOrder) {
		this.doneCastTime = false;
		this.doneEffect = false;
		this.castStartTick = 0;
		this.castTimeEndTick = this.ability.ignoreCastTime() ? 0
				: (int) (this.ability.getCastTime() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.castPointTicks = this.castTimeEndTick
				+ (int) (this.unit.getUnitType().getCastPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.backswingTicks = this.castPointTicks
				+ (int) (this.unit.getUnitType().getCastBackswingPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		this.preventReInterrupt = false;
		this.autoOrder = autoOrder;
		return innerReset(game, target, false);
	}

	public CBehavior reset(final CSimulation game, final AbilityPointTarget target, final boolean autoOrder) {
		this.doneCastTime = false;
		this.doneEffect = false;
		this.castStartTick = 0;
		this.castTimeEndTick = this.ability.ignoreCastTime() ? 0
				: (int) (this.ability.getCastTime() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.castPointTicks = this.castTimeEndTick
				+ (int) (this.unit.getUnitType().getCastPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.backswingTicks = this.castPointTicks
				+ (int) (this.unit.getUnitType().getCastBackswingPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = this.ability.getBaseOrderId();
		this.preventReInterrupt = false;
		this.autoOrder = autoOrder;
		return innerReset(game, target, false);
	}

	public CBehavior reset(final CSimulation game, final AbilityPointTarget target, int orderId,
			final boolean autoOrder) {
		this.doneCastTime = false;
		this.doneEffect = false;
		this.castStartTick = 0;
		this.castTimeEndTick = this.ability.ignoreCastTime() ? 0
				: (int) (this.ability.getCastTime() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.castPointTicks = this.castTimeEndTick
				+ (int) (this.unit.getUnitType().getCastPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.backswingTicks = this.castPointTicks
				+ (int) (this.unit.getUnitType().getCastBackswingPoint() / WarsmashConstants.SIMULATION_STEP_TIME);
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		this.orderId = orderId;
		this.preventReInterrupt = false;
		this.autoOrder = autoOrder;
		return innerReset(game, target, false);
	}

	public ABBehavior reset(final boolean autoOrder) {
		return null;
	}

	public ABBehavior reset(int orderId, final boolean autoOrder) {
		return null;
	}

	@Override
	public CBehavior update(final CSimulation game, boolean withinFacingWindow) {
		boolean wasChanneling = this.channeling;
		if (this.castStartTick == 0) {
			CBehavior prevBeh = this.unit.getCurrentBehavior();

			this.castStartTick = game.getGameTurnTick();
			this.castBehaviorNotifyTick = (int) (this.castStartTick + 0.5 / WarsmashConstants.SIMULATION_STEP_TIME);

			if (!this.target.visit(this.preCastTargetableVisitor.reset(game, this.unit, ability, false, orderId))) {
				cleanupInputs();
				return this.unit.pollNextOrderBehavior(game);
			}

			this.ability.checkCanUse(game, unit, orderId, autoOrder, BooleanAbilityActivationReceiver.INSTANCE);
			if (!BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
				cleanupInputs();
				return this.unit.pollNextOrderBehavior(game);
			}

			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.ability, this.target);
			if (this.unit.getCurrentBehavior() != prevBeh) {
				cleanupInputs();
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}

			if (!instant) {
				if (this.castPointTicks > this.castTimeEndTick) {
					EnumSet<SecondaryTag> tags = this.ability.getCastingSecondaryTags().clone();
					tags.add(SecondaryTag.CHANNEL);
					this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(), tags,
							1.0f, true);
				}
			}
		}

		if (instant) {
			if (!this.doneCastTime) {
				// should have already checked castable/range above, as no time delay
				if (!this.unit.chargeMana(this.ability.getChargedManaCost())) {
					cleanupInputs();
					return this.unit.pollNextOrderBehavior(game);
				}
				this.ability.startCooldown(game, this.unit);

				this.ability.runBeginCastingActions(game, unit, orderId);
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, this.target);
				this.doneCastTime = true;
			}
			CBehavior beh = tryDoEffect(game, wasChanneling);
			if (beh != null) {
				cleanupInputs();
				return beh;
			}
			CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (newBehavior != null) {
				localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				return newBehavior;
			}

			if (!this.channeling) {
				cleanupInputs();
				return this.unit.pollNextOrderBehavior(game);
			}
		} else {
			final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
			if (!this.doneCastTime && ticksSinceCast >= this.castTimeEndTick) {
				if (!this.isWithinRange(game)) {
					// target moved too far, out of range now
					cleanupInputs();
					return this.unit.pollNextOrderBehavior(game);
				}

				this.ability.checkCanUse(game, unit, orderId, autoOrder, BooleanAbilityActivationReceiver.INSTANCE);
				if (!BooleanAbilityActivationReceiver.INSTANCE.isOk()
						|| !this.unit.chargeMana(this.ability.getChargedManaCost())) {
					cleanupInputs();
					return this.unit.pollNextOrderBehavior(game);
				}
				this.ability.startCooldown(game, this.unit);
				CBehavior prevBeh = this.unit.getCurrentBehavior();

				this.ability.runBeginCastingActions(game, unit, orderId);
				CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
				if (newBehavior != null) {
					localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
					return newBehavior;
				}

				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, this.target);
				if (this.unit.getCurrentBehavior() != prevBeh) {
					cleanupInputs();
					return this.unit.getCurrentBehavior();
				} else if (this.unit.isPaused()) {
					return this;
				}

				this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
						this.ability.getCastingSecondaryTags(), 1.0f, true);
				this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
				this.doneCastTime = true;
			}

			if ((ticksSinceCast >= castPointTicks)) {
				if (this.castPointTicks > this.castTimeEndTick && !this.isWithinRange(game)) {
					// Unit moved too far, out of range now
					cleanupInputs();
					return this.unit.pollNextOrderBehavior(game);
				}
				CBehavior beh = tryDoEffect(game, wasChanneling);
				if (beh != null) {
					cleanupInputs();
					return beh;
				}
				CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
				if (newBehavior != null) {
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
		this.ability.cleanupInputs(castId);
	}

	private CBehavior tryDoEffect(CSimulation game, boolean wasChanneling) {
		boolean wasEffectDone = this.doneEffect;
		if (!wasEffectDone) {
			CBehavior prevBeh = this.unit.getCurrentBehavior();
			if (this.channeling) {
				game.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
			} else {
				game.unitSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			this.doneEffect = true;

			if (this.unit.getCurrentBehavior() != prevBeh) {
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}

			this.ability.runEndCastingActions(game, unit, orderId);
			this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);

			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this.ability, this.target);
			if (this.unit.getCurrentBehavior() != prevBeh) {
				return this.unit.getCurrentBehavior();
			} else if (this.unit.isPaused()) {
				return this;
			}

		}
		this.channeling = this.channeling && this.doChannelTick(game, this.unit, this.target);
		if (wasEffectDone && wasChanneling && !this.channeling) {
			endChannel(game, false);
			return this.unit.pollNextOrderBehavior(game);
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
		Boolean preventEndEvents = (Boolean) this.localStore.get(ABLocalStoreKeys.PREVENTENDEVENTS + this.castId);
		if (preventEndEvents == null || !preventEndEvents) {
			if (!interrupted) {
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.ability, this.target);
			}
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.ability, this.target);
		}
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
		float range = this.ability.getCastRange();
		if (this.castStartTick > 0) {
			range += simulation.getGameplayConstants().getSpellCastRangeBuffer();
		}
		return this.unit.canReach(this.target, range);
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
		return this.doneEffect || this.target.visit(
				this.preCastTargetableVisitor.reset(simulation, this.unit, this.ability, this.channeling, orderId));
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
		return this.doneEffect;
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
		return ability;
	}

}
