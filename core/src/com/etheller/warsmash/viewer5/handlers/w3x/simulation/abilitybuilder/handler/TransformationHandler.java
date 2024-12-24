package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.GetInstantTransformationBuffVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTransformationBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.AltitudeAdjustmentTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.DelayTimerTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.TransformationMorphAnimationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class TransformationHandler {

	public static void setUnitID(final CSimulation game, final Map<String, Object> localStore, final CUnit unit,
			final CUnitType newType, final boolean addAlternateTagAfter, final OnTransformationActions actions,
			final CAbility ability) {
		setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability, false);
	}

	public static void setUnitID(final CSimulation game, final Map<String, Object> localStore, final CUnit unit,
			final CUnitType newType, final boolean addAlternateTagAfter, final OnTransformationActions actions,
			final CAbility ability, final boolean updateArt) {
		final CPlayer pl = game.getPlayer(unit.getPlayerIndex());
		if (actions != null) {
			pl.setGold(Math.max(pl.getGold() - actions.goldCost, 0));
			pl.setLumber(Math.max(pl.getLumber() - actions.lumberCost, 0));

			if (!addAlternateTagAfter) {
				if (actions.onUntransformActions != null) {
					for (final ABAction action : actions.onUntransformActions) {
						action.runAction(game, unit, localStore, 0);
					}
				}
			}
		}

		unit.setTypeId(game, newType.getTypeId(), updateArt);
		pl.setUnitFoodUsed(unit, newType.getFoodUsed());
		pl.setUnitFoodMade(unit, newType.getFoodMade());
		if (addAlternateTagAfter) {
			if (unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE)) {
				unit.getUnitAnimationListener().forceResetCurrentAnimation();
			}
		}
		else {
			if (unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE)) {
				unit.getUnitAnimationListener().forceResetCurrentAnimation();
			}
		}
		game.unitSoundEffectEvent(unit, ability.getAlias());

		if (addAlternateTagAfter && (actions != null)) {
			if (actions.onTransformActions != null) {
				for (final ABAction action : actions.onTransformActions) {
					action.runAction(game, unit, localStore, 0);
				}
			}
		}
	}

	public static void playMorphAnimation(final CUnit unit, final boolean addAlternateTagAfter) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, SequenceUtils.EMPTY, 1.0f, true);
		}
		else {
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.MORPH, EnumSet.of(SecondaryTag.ALTERNATE),
					1.0f, true);
		}
		unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, true);
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
		}
		else {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
		}
	}

	public static void setTags(final CUnit unit, final boolean addAlternateTagAfter) {
		if (addAlternateTagAfter) {
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
		}
		else {
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
		}
		unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
				addAlternateTagAfter ? EnumSet.of(SecondaryTag.ALTERNATE) : SequenceUtils.EMPTY, 1.0f, true);
	}

	public static void beginTakingOff(final CSimulation game, final Map<String, Object> localStore, final CUnit unit,
			final CUnitType newType, final OnTransformationActions actions, final CAbility ability,
			final boolean addAlternateTagAfter, final boolean immediateTakeoff, final float altitudeAdjustmentDelay,
			final float altitudeAdjustmentDuration) {
		CTimer timer = (CTimer) localStore.get(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT);
		if (timer != null) {
			game.unregisterTimer(timer);
		}
		timer = (new DelayTimerTimer(
				new AltitudeAdjustmentTimer(game, unit, newType.getDefaultFlyingHeight(), altitudeAdjustmentDuration),
				localStore, altitudeAdjustmentDelay));
		timer.start(game);
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
		TransformationHandler.setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability);
		if (immediateTakeoff) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
		else {
			final CTimer t2 = new TransformationMorphAnimationTimer(game, unit, addAlternateTagAfter,
					altitudeAdjustmentDelay);
			t2.start(game);
			localStore.put(ABLocalStoreKeys.WAITING_ANIMATION, t2);
		}
	}

	public static void beginLanding(final CSimulation game, final Map<String, Object> localStore, final CUnit unit,
			final CUnitType newType, final boolean addAlternateTagAfter, final boolean immediateLanding,
			final float landingDelay, final float altitudeAdjustmentDuration) {
		unit.setFacing(225);
		if (immediateLanding) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
		else {
			final CTimer timer = new TransformationMorphAnimationTimer(game, unit, addAlternateTagAfter, landingDelay);
			timer.start(game);
			localStore.put(ABLocalStoreKeys.WAITING_ANIMATION, timer);
		}
		CTimer timer = (CTimer) localStore.get(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT);
		if (timer != null) {
			game.unregisterTimer(timer);
		}
		timer = new AltitudeAdjustmentTimer(game, unit, newType.getDefaultFlyingHeight(), altitudeAdjustmentDuration);
		timer.start(game);
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
	}

	public static void startSlowTransformation(final CSimulation game, final Map<String, Object> localStore,
			final CUnit unit, final CUnitType newType, final OnTransformationActions actions, final CAbility ability,
			final boolean addAlternateTagAfter, final boolean takingOff, final boolean landing,
			final boolean immediateTakeoff, final boolean immediateLanding, final float altitudeAdjustmentDelay,
			final float landingDelay, final float altitudeAdjustmentDuration) {
		final CTimer timer = (CTimer) localStore.get(ABLocalStoreKeys.WAITING_ANIMATION);
		if (timer != null) {
			game.unregisterTimer(timer);
		}
		unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND,
				addAlternateTagAfter ? SequenceUtils.EMPTY : EnumSet.of(SecondaryTag.ALTERNATE), true);
		if (takingOff || landing) {

			if (takingOff) {
				TransformationHandler.beginTakingOff(game, localStore, unit, newType, actions, ability,
						addAlternateTagAfter, immediateTakeoff, altitudeAdjustmentDelay, altitudeAdjustmentDuration);
			}

			if (landing) {
				TransformationHandler.beginLanding(game, localStore, unit, newType, addAlternateTagAfter,
						immediateLanding, landingDelay, altitudeAdjustmentDuration);
			}
		}
		else {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
	}

	public static void finishSlowTransformation(final CSimulation game, final Map<String, Object> localStore,
			final CUnit unit, final CUnitType newType, final OnTransformationActions actions,
			final AbilityBuilderAbility ability, final boolean addAlternateTagAfter, final boolean permanent,
			final boolean takingOff) {
		if (!takingOff) {
			TransformationHandler.setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability);
		}
		if (permanent) {
			unit.remove(game, ability);
		}
	}

	public static void instantTransformation(final CSimulation game, final Map<String, Object> localStore,
			final CUnit unit, final CUnitType newType, final OnTransformationActions actions,
			final AbilityBuilderAbility ability, final boolean addAlternateTagAfter, final boolean permanent,
			final boolean playMorph) {
		if (newType.getTypeId().equals(unit.getTypeId())) {
			return;
		}
		System.err.println("setting " + newType.getTypeId() + " on " + unit.getTypeId());
		setUnitID(game, localStore, unit, newType, addAlternateTagAfter, actions, ability, false);
		if (playMorph) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
		}
		if (permanent) {
			unit.remove(game, ability);
		}
	}

	public static void createSlowTransformBackBuff(final CSimulation game, final Map<String, Object> localStore,
			final CUnit unit, final CUnitType newType, final OnTransformationActions actions,
			final AbilityBuilderActiveAbility ability, final War3ID buffId, final boolean addAlternateTagAfter,
			final float transformationTime, final float duration, final boolean permanent, final boolean takingOff,
			final boolean landing, final boolean immediateTakeoff, final boolean immediateLanding,
			final float altitudeAdjustmentDelay, final float landingDelay, final float altitudeAdjustmentDuration) {
		if (addAlternateTagAfter && (duration > 0)) {
			unit.add(game,
					new ABTimedTransformationBuff(game.getHandleIdAllocator().createId(), localStore, actions,
							buffId == null ? ability.getAlias() : buffId, duration, ability, newType,
							!addAlternateTagAfter, permanent, duration, transformationTime, landingDelay,
							altitudeAdjustmentDelay, altitudeAdjustmentDuration, immediateLanding, immediateTakeoff));
		}
	}

	public static void createInstantTransformBackBuff(final CSimulation game, final Map<String, Object> localStore,
			final CUnit unit, final CUnitType newType, final OnTransformationActions actions,
			final AbilityBuilderAbility ability, final War3ID buffId, final boolean addAlternateTagAfter,
			final float transformationTime, final float duration, final boolean permanent) {
		if (addAlternateTagAfter && (duration > 0)) {
			final ABBuff thebuff = ability
					.visit(GetInstantTransformationBuffVisitor.getInstance().reset(game, localStore, newType, actions,
							buffId, addAlternateTagAfter, transformationTime, duration, permanent));
			if (thebuff != null) {
				unit.add(game, thebuff);
			}
		}
	}

	public static class OnTransformationActions {
		private int goldCost;
		private int lumberCost;
		private Integer foodCost;
		private List<ABAction> onTransformActions;
		private List<ABAction> onUntransformActions;

		public OnTransformationActions(final List<ABAction> onUntransformActions) {
			this.onUntransformActions = onUntransformActions;
		}

		public OnTransformationActions(final int goldCost, final int lumberCost, final Integer foodCost,
				final List<ABAction> onTransformActions, final List<ABAction> onUntransformActions) {
			this.goldCost = goldCost;
			this.lumberCost = lumberCost;
			this.foodCost = foodCost;
			this.onTransformActions = onTransformActions;
			this.onUntransformActions = onUntransformActions;
		}

		public OnTransformationActions createUntransformActions() {
			return new OnTransformationActions(-this.goldCost, -this.lumberCost,
					this.foodCost != null ? -this.foodCost : null, null, this.onUntransformActions);
		}

		public void setOnUntransformActions(final List<ABAction> onUntransformActions) {
			this.onUntransformActions = onUntransformActions;
		}
	}

}
