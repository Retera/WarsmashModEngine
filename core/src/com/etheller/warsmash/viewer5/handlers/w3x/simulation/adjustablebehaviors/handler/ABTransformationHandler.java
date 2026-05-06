package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABTimedTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABAltitudeAdjustmentTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABDelayTimerTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABTransformationMorphAnimationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.visitor.ABGetInstantTransformationBuffVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABTransformationHandler {

	public static void setUnitID(final ABLocalDataStore localStore, final CUnit unit, final CUnitType newType,
			final boolean keepRatios, final boolean addAlternateTagAfter, final OnTransformationActions actions,
			final CAbility ability) {
		setUnitID(localStore, unit, newType, keepRatios, addAlternateTagAfter, actions, ability, false);
	}

	public static void setUnitID(final ABLocalDataStore localStore, final CUnit unit, final CUnitType newType,
			final boolean keepRatios, final boolean addAlternateTagAfter, final OnTransformationActions actions,
			final CAbility ability, final boolean updateArt) {
		final CPlayer pl = localStore.game.getPlayer(unit.getPlayerIndex());
		if (actions != null) {
			pl.setGold(Math.max(pl.getGold() - actions.goldCost, 0));
			pl.setLumber(Math.max(pl.getLumber() - actions.lumberCost, 0));

			if (!addAlternateTagAfter) {
				if (actions.onUntransformActions != null) {
					for (final ABAction action : actions.onUntransformActions) {
						action.runAction(unit, localStore, actions.castId);
					}
				}
			}
		}

		unit.setTypeId(localStore.game, newType.getTypeId(), keepRatios, updateArt);
		pl.setUnitFoodUsed(unit, newType.getFoodUsed());
		pl.setUnitFoodMade(unit, newType.getFoodMade());
		if (addAlternateTagAfter) {
			if (unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE)) {
				unit.getUnitAnimationListener().forceResetCurrentAnimation();
			}
		} else {
			if (unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE)) {
				unit.getUnitAnimationListener().forceResetCurrentAnimation();
			}
		}
		localStore.game.unitSoundEffectEvent(unit, ability.getAlias());

		if (addAlternateTagAfter && (actions != null)) {
			if (actions.onTransformActions != null) {
				for (final ABAction action : actions.onTransformActions) {
					action.runAction(unit, localStore, actions.castId);
				}
			}
		}
	}

	public static void playMorphAnimation(final CUnit unit, final boolean addAlternateTagAfter) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, SequenceUtils.EMPTY, 1.0f, true);
		} else {
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, EnumSet.of(SecondaryTag.ALTERNATE),
					1.0f, true);
		}
		unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, true);
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
		} else {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
		}
	}

	public static void setTags(final CUnit unit, final boolean addAlternateTagAfter) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
		} else {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
		}
		unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
				addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, 1.0f, true);
	}

	public static void beginTakingOff(final ABLocalDataStore localStore, final CUnit unit, final CUnitType newType,
			final boolean keepRatios, final OnTransformationActions actions, final CAbility ability,
			final boolean addAlternateTagAfter, final boolean immediateTakeoff, final float altitudeAdjustmentDelay,
			final float altitudeAdjustmentDuration) {
		CTimer timer = localStore.get(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, CTimer.class);
		if (timer != null) {
			localStore.game.unregisterTimer(timer);
		}
		timer = (new ABDelayTimerTimer(
				new ABAltitudeAdjustmentTimer(unit, newType.getDefaultFlyingHeight(), altitudeAdjustmentDuration),
				localStore, altitudeAdjustmentDelay));
		timer.start(localStore.game);
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
		ABTransformationHandler.setUnitID(localStore, unit, newType, keepRatios, addAlternateTagAfter, actions, ability);
		if (immediateTakeoff) {
			ABTransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		} else {
			final CTimer t2 = new ABTransformationMorphAnimationTimer(unit, addAlternateTagAfter,
					altitudeAdjustmentDelay);
			t2.start(localStore.game);
			localStore.put(ABLocalStoreKeys.WAITING_ANIMATION, t2);
		}
	}

	public static void beginLanding(final ABLocalDataStore localStore, final CUnit unit, final CUnitType newType,
			final boolean addAlternateTagAfter, final boolean immediateLanding, final float landingDelay,
			final float altitudeAdjustmentDuration) {
		unit.setFacing(225);
		if (immediateLanding) {
			ABTransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		} else {
			final CTimer timer = new ABTransformationMorphAnimationTimer(unit, addAlternateTagAfter, landingDelay);
			timer.start(localStore.game);
			localStore.put(ABLocalStoreKeys.WAITING_ANIMATION, timer);
		}
		CTimer timer = localStore.get(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, CTimer.class);
		if (timer != null) {
			localStore.game.unregisterTimer(timer);
		}
		timer = new ABAltitudeAdjustmentTimer(unit, newType.getDefaultFlyingHeight(), altitudeAdjustmentDuration);
		timer.start(localStore.game);
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
	}

	public static void startSlowTransformation(final ABLocalDataStore localStore, final CUnit unit,
			final CUnitType newType, final boolean keepRatios, final OnTransformationActions actions,
			final CAbility ability, final boolean addAlternateTagAfter, final boolean takingOff, final boolean landing,
			final boolean immediateTakeoff, final boolean immediateLanding, final float altitudeAdjustmentDelay,
			final float landingDelay, final float altitudeAdjustmentDuration) {
		final CTimer timer = localStore.get(ABLocalStoreKeys.WAITING_ANIMATION, CTimer.class);
		if (timer != null) {
			localStore.game.unregisterTimer(timer);
		}
		unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				addAlternateTagAfter ? SequenceUtils.EMPTY : EnumSet.of(SecondaryTag.ALTERNATE), true);
		if (takingOff || landing) {

			if (takingOff) {
				ABTransformationHandler.beginTakingOff(localStore, unit, newType, keepRatios, actions, ability,
						addAlternateTagAfter, immediateTakeoff, altitudeAdjustmentDelay, altitudeAdjustmentDuration);
			}

			if (landing) {
				ABTransformationHandler.beginLanding(localStore, unit, newType, addAlternateTagAfter, immediateLanding,
						landingDelay, altitudeAdjustmentDuration);
			}
		} else {
			ABTransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
	}

	public static void finishSlowTransformation(final ABLocalDataStore localStore, final CUnit unit,
			final CUnitType newType, final boolean keepRatios, final OnTransformationActions actions,
			final ABAbilityBuilderAbility ability, final boolean addAlternateTagAfter, final boolean permanent,
			final boolean takingOff) {
		if (!takingOff) {
			ABTransformationHandler.setUnitID(localStore, unit, newType, keepRatios, addAlternateTagAfter, actions,
					ability);
		}
		if (permanent) {
			unit.remove(localStore.game, ability);
		}
	}

	public static void instantTransformation(final ABLocalDataStore localStore, final CUnit unit, final CUnitType newType,
			final boolean keepRatios, final OnTransformationActions actions, final ABAbilityBuilderAbility ability,
			final boolean addAlternateTagAfter, final boolean permanent, final boolean playMorph) {
		if (newType.getTypeId().equals(unit.getTypeId())) {
			return;
		}
		setUnitID(localStore, unit, newType, keepRatios, addAlternateTagAfter, actions, ability, false);
		if (playMorph) {
			ABTransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
		if (permanent) {
			unit.remove(localStore.game, ability);
		}
	}

	public static void createSlowTransformBackBuff(CUnit sourceUnit, final ABLocalDataStore localStore, final CUnit unit,
			final CUnitType newType, final boolean keepRatios, final OnTransformationActions actions,
			final ABAbilityBuilderAbility ability, final War3ID buffId, final boolean addAlternateTagAfter,
			final float transformationTime, final float duration, final boolean permanent, final boolean takingOff,
			final boolean landing, final boolean immediateTakeoff, final boolean immediateLanding,
			final float altitudeAdjustmentDelay, final float landingDelay, final float altitudeAdjustmentDuration) {
		if (addAlternateTagAfter && (duration > 0)) {
			unit.add(localStore.game,
					new ABTimedTransformationBuff(localStore.game.getHandleIdAllocator().createId(), localStore,
							ability, sourceUnit, actions, buffId == null ? ability.getAlias() : buffId, duration,
							ability, newType, keepRatios, !addAlternateTagAfter, permanent, duration,
							transformationTime, landingDelay, altitudeAdjustmentDelay, altitudeAdjustmentDuration,
							immediateLanding, immediateTakeoff));
		}
	}

	public static void createInstantTransformBackBuff(CUnit sourceUnit, final ABLocalDataStore localStore,
			final CUnit unit, final CUnitType newType, final boolean keepRatios, final OnTransformationActions actions,
			final ABAbilityBuilderAbility ability, final War3ID buffId, final boolean addAlternateTagAfter,
			final float transformationTime, final float duration, final boolean permanent) {
		if (addAlternateTagAfter && (duration > 0)) {
			final ABBuff thebuff = ability.visit(ABGetInstantTransformationBuffVisitor.getInstance().reset(
					localStore.game, sourceUnit, localStore, newType, keepRatios, actions, buffId, addAlternateTagAfter,
					transformationTime, duration, permanent));
			if (thebuff != null) {
				unit.add(localStore.game, thebuff);
			}
		}
	}

	public static class OnTransformationActions {
		private int goldCost;
		private int lumberCost;
		private Integer foodCost;
		private List<ABAction> onTransformActions;
		private List<ABAction> onUntransformActions;
		private int castId;

		public OnTransformationActions(final List<ABAction> onUntransformActions, int castId) {
			this.onUntransformActions = onUntransformActions;
			this.castId = castId;
		}

		public OnTransformationActions(final int goldCost, final int lumberCost, final Integer foodCost,
				final List<ABAction> onTransformActions, final List<ABAction> onUntransformActions, int castId) {
			this.goldCost = goldCost;
			this.lumberCost = lumberCost;
			this.foodCost = foodCost;
			this.onTransformActions = onTransformActions;
			this.onUntransformActions = onUntransformActions;
			this.castId = castId;
		}

		public OnTransformationActions createUntransformActions() {
			return new OnTransformationActions(-this.goldCost, -this.lumberCost,
					this.foodCost != null ? -this.foodCost : null, null, this.onUntransformActions, this.castId);
		}

		public void setOnUntransformActions(final List<ABAction> onUntransformActions) {
			this.onUntransformActions = onUntransformActions;
		}

		public int getCastId() {
			return this.castId;
		}
	}

}
