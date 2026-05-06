package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior;

import java.util.EnumSet;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;

public class ABBehaviorAbilityBuilderNoTarget implements ABBehavior {
	private ABLocalDataStore localStore;
	private ABAbilityBuilderActiveAbility ability;

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
	private EnumSet<SecondaryTag> channelTags;
	private boolean firstAnimation;

	public ABBehaviorAbilityBuilderNoTarget(final CUnit unit, final ABLocalDataStore localStore,
			ABAbilityBuilderActiveAbility ability) {
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
		this.firstAnimation = true;
		this.autoOrder = autoOrder;

		this.channelTags = this.ability.getCastingSecondaryTags().clone();
		this.channelTags.add(SecondaryTag.CHANNEL);
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
		this.firstAnimation = true;
		this.autoOrder = autoOrder;

		this.channelTags = this.ability.getCastingSecondaryTags().clone();
		this.channelTags.add(SecondaryTag.CHANNEL);
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		boolean wasChanneling = this.channeling;
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();

			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.ability, null);
			// In war3, non-targeted spells cannot be interrupted by pausing on spell events

			if (!instant && this.castTimeEndTick > 0) {
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
						EnumSet.of(SecondaryTag.CHANNEL), 1.0f, true);
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
			CBehavior newBehavior = localStore.get(ABLocalStoreKeys.NEWBEHAVIOR, CBehavior.class);
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
				CBehavior newBehavior = localStore.get(ABLocalStoreKeys.NEWBEHAVIOR, CBehavior.class);
				if (newBehavior != null) {
					localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
					return newBehavior;
				}

				this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.ability, null);

				this.channeling = localStore.getBoolean(ABLocalStoreKeys.CHANNELING);
				if (!this.channeling) {
					this.unit.getUnitAnimationListener().playAnimation(this.firstAnimation, this.ability.getCastingPrimaryTag(),
							this.ability.getCastingSecondaryTags(), 1.0f, true);
					this.firstAnimation = false;
					this.unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND, SequenceUtils.EMPTY, true);
				}
				this.doneCastTime = true;
			}
			if (this.channeling) {
				this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
						this.channelTags, 1.0f, true);
			}
			if ((ticksSinceCast >= castPointTicks)) {
				CBehavior beh = tryDoEffect(game, wasChanneling);
				if (beh != null) {
					cleanupInputs();
					return beh;
				}
				CBehavior newBehavior = localStore.get(ABLocalStoreKeys.NEWBEHAVIOR, CBehavior.class);
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
			this.channeling = localStore.getBoolean(ABLocalStoreKeys.CHANNELING);
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
		if (ability.getItem() != null && ability.getItem().getItemType().isActivelyUsed()) {
			// This is for runes/glyphs that use on pickup. We will never see an update loop
			// for these

			if (!this.unit.chargeMana(this.ability.getChargedManaCost())) {
				cleanupInputs();
				this.unit.beginBehavior(game, this.unit.pollNextOrderBehavior(game));
				return;
			}
			this.ability.startCooldown(game, this.unit);

			this.ability.runBeginCastingActions(game, unit, orderId);

			CBehavior beh = tryDoEffect(game, false);
			if (beh != null) {
				cleanupInputs();
				this.unit.beginBehavior(game, beh);
				return;
			}
			CBehavior newBehavior = localStore.get(ABLocalStoreKeys.NEWBEHAVIOR, CBehavior.class);
			if (newBehavior != null) {
				localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				this.unit.beginBehavior(game, newBehavior);
				return;
			}

			cleanupInputs();
			//this.unit.beginBehavior(game, this.unit.pollNextOrderBehavior(game));
		}
	}

	public boolean doChannelTick(CSimulation game, CUnit caster) {
		this.ability.runChannelTickActions(game, caster, orderId);
		return localStore.getBoolean(ABLocalStoreKeys.CHANNELING);
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		checkEndChannel(game, interrupted);
		Boolean preventEndEvents = this.localStore.get(ABLocalStoreKeys.PREVENTENDEVENTS + this.castId, Boolean.class);
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

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
