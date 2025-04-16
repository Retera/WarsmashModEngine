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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;

public class CBehaviorAbilityBuilderNoTarget implements ABBehavior {
	private Map<String, Object> localStore;
	private AbilityBuilderActiveAbility ability;

	private int castStartTick = 0;
	private int castTimeEndTick = 0;
	private int castPointTicks = 0;
	private int backswingTicks = 0;
	private boolean doneCastTime = false;
	private boolean doneEffect = false;
	private boolean channeling = false;

	private CUnit unit;
	private int orderId;
	private boolean autoOrder;

	private boolean instant = false;
	private CBehaviorCategory behaviorCategory = null;
	private int castId = 0;

	public CBehaviorAbilityBuilderNoTarget(final CUnit unit, final Map<String, Object> localStore,
			AbilityBuilderActiveAbility ability) {
		this.unit = unit;
		this.localStore = localStore;
		this.ability = ability;
	}

	public void setInstant(boolean instant) {
		this.instant = instant;
	}

	public void setBehaviorCategory(CBehaviorCategory behaviorCategory) {
		this.behaviorCategory = behaviorCategory;
	}

	@Override
	public ABBehavior reset(final CSimulation game, final CWidget target, final boolean autoOrder) {
		return null;
	}

	@Override
	public ABBehavior reset(final CSimulation game, CWidget target, int orderId, final boolean autoOrder) {
		return null;
	}

	@Override
	public ABBehavior reset(final CSimulation game, final AbilityPointTarget target, final boolean autoOrder) {
		return null;
	}

	@Override
	public ABBehavior reset(final CSimulation game, AbilityPointTarget target, int orderId, final boolean autoOrder) {
		return null;
	}

	@Override
	public ABBehavior reset(final boolean autoOrder) {
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
		this.autoOrder = autoOrder;
		return this;
	}

	@Override
	public ABBehavior reset(int orderId, final boolean autoOrder) {
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
		this.autoOrder = autoOrder;
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		boolean wasChanneling = this.channeling;
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();

			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.ability, null);
			// In war3, non-targeted spells cannot be interrupted by pausing on spell events

			if (!instant) {
				if (this.castPointTicks > this.castTimeEndTick) {
					EnumSet<SecondaryTag> tags = this.ability.getCastingSecondaryTags().clone();
					tags.add(SecondaryTag.CHANNEL);
					this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
							tags, 1.0f, true);
				}
			}
		}

		if (instant) {
			if (!this.doneCastTime) {
				// should have already checked castable above, as no time delay
				if (!this.unit.chargeMana(this.ability.getChargedManaCost())) {
					cleanupInputs();
					return this.unit.pollNextOrderBehavior(game);
				}
				this.ability.startCooldown(game, this.unit);
	
				this.ability.runBeginCastingActions(game, unit, orderId);
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, null);
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
				this.ability.checkCanUse(game, unit, orderId, autoOrder, BooleanAbilityActivationReceiver.INSTANCE);
				if (!BooleanAbilityActivationReceiver.INSTANCE.isOk()
						|| !this.unit.chargeMana(this.ability.getChargedManaCost())) {
					cleanupInputs();
					return this.unit.pollNextOrderBehavior(game);
				}
				this.ability.startCooldown(game, this.unit);

				this.ability.runBeginCastingActions(game, unit, orderId);
				CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
				if (newBehavior != null) {
					localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
					return newBehavior;
				}
				
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, null);

				this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
						this.ability.getCastingSecondaryTags(), 1.0f, true);
				this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
				this.doneCastTime = true;
			}
			if ((ticksSinceCast >= castPointTicks)) {
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

	private CBehavior tryDoEffect(CSimulation game, boolean wasChanneling) {
		boolean wasEffectDone = this.doneEffect;
		if (!wasEffectDone) {
			if (this.channeling) {
				game.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
			} else {
				game.unitSoundEffectEvent(this.unit, this.ability.getAlias());
			}
			this.doneEffect = true;

			this.ability.runEndCastingActions(game, unit, orderId);
			this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this.ability, null);
		}
		this.channeling = this.channeling && this.doChannelTick(game, this.unit);
		if (wasEffectDone && wasChanneling && !this.channeling) {
			endChannel(game, false);
			return this.unit.pollNextOrderBehavior(game);
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
		Boolean preventEndEvents = (Boolean) this.localStore.get(ABLocalStoreKeys.PREVENTENDEVENTS+this.castId);
		if (preventEndEvents == null || !preventEndEvents) {
			if (!interrupted) {
				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.ability, null);
			}
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.ability, null);
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
	}
	
	private void cleanupInputs() {
		this.ability.cleanupInputs(this.castId);
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	public void setCastId(int castId) {
		this.castId  = castId;
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

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
