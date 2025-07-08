package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CBehaviorFinishTransformation implements CBehavior {
	private CUnit sourceUnit;
	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private CUnit unit;
	private AbilityBuilderAbility ability;
	private CUnitType baseTypeForDuration;
	private CUnitType newType;
	private boolean keepRatios;
	private int visibleOrderId;
	private boolean permanent;
	private float duration;
	private int transformationTickDuration;
	private float altitudeAdjustmentDelay;
	private float altitudeAdjustmentDuration;
	private float landingDelay;

	private boolean immediateLanding;
	private boolean immediateTakeoff;

	private boolean addAlternateTagAfter;

	private boolean takingOff;
	private boolean landing;

	private War3ID buffId;
	private float transformationTime;
	private boolean instantTransformAtDurationEnd;

	private int castStartTick = 0;

	public CBehaviorFinishTransformation(CUnit sourceUnit, Map<String, Object> localStore, final CUnit unit,
			AbilityBuilderAbility ability, CUnitType newType, final boolean keepRatios, OnTransformationActions actions,
			boolean addAlternateTagAfter, final int visibleOrderId, boolean permanent, float duration,
			float transformationTime, float landingDelay, float altitudeAdjustmentDelay,
			float altitudeAdjustmentDuration, boolean immediateLanding, boolean immediateTakeoff, War3ID buffId,
			CUnitType baseTypeForDuration, boolean instantTransformAtDurationEnd) {
		this.sourceUnit = sourceUnit;
		this.localStore = localStore;
		this.actions = actions;
		this.unit = unit;
		this.ability = ability;
		this.newType = newType;
		this.keepRatios = keepRatios;
		this.visibleOrderId = visibleOrderId;
		this.permanent = permanent;
		this.duration = duration;
		this.transformationTime = transformationTime;
		// Minus one tick, as we need to wait one tick to start this behavior
		this.transformationTickDuration = Math.round(transformationTime / WarsmashConstants.SIMULATION_STEP_TIME) - 1;
		this.altitudeAdjustmentDelay = altitudeAdjustmentDelay;
		this.altitudeAdjustmentDuration = altitudeAdjustmentDuration;
		this.landingDelay = landingDelay;

		this.immediateLanding = immediateLanding;
		this.immediateTakeoff = immediateTakeoff;

		this.addAlternateTagAfter = addAlternateTagAfter;

		this.buffId = buffId;
		this.baseTypeForDuration = baseTypeForDuration;
		this.instantTransformAtDurationEnd = instantTransformAtDurationEnd;

		this.takingOff = unit.getMovementType() != MovementType.FLY && newType.getMovementType() == MovementType.FLY;
		this.landing = unit.getMovementType() == MovementType.FLY && newType.getMovementType() != MovementType.FLY;

		if (this.landing) {
			this.transformationTickDuration += this.landingDelay > 0
					? Math.round(altitudeAdjustmentDuration / WarsmashConstants.SIMULATION_STEP_TIME)
					: 0;
		}
	}

	@Override
	public CBehavior update(CSimulation game) {
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();
			TransformationHandler.startSlowTransformation(game, localStore, unit, newType, keepRatios, actions, ability,
					addAlternateTagAfter, takingOff, landing, immediateTakeoff, immediateLanding,
					altitudeAdjustmentDelay, landingDelay, altitudeAdjustmentDuration);
		}

		final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
		if (ticksSinceCast >= this.transformationTickDuration) {
			TransformationHandler.finishSlowTransformation(game, localStore, unit, newType, keepRatios, actions,
					ability, addAlternateTagAfter, permanent, takingOff);

			if (instantTransformAtDurationEnd) {
				TransformationHandler.createInstantTransformBackBuff(game, sourceUnit, localStore, unit, baseTypeForDuration,
						keepRatios, actions.createUntransformActions(), ability, buffId, addAlternateTagAfter,
						transformationTime, duration, permanent);
			} else {
				TransformationHandler.createSlowTransformBackBuff(game, sourceUnit, localStore, unit, baseTypeForDuration,
						keepRatios, actions.createUntransformActions(), ability, buffId, addAlternateTagAfter,
						transformationTime, duration, permanent, takingOff, landing, immediateTakeoff, immediateLanding,
						altitudeAdjustmentDelay, landingDelay, altitudeAdjustmentDuration);
			}

			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.ability, null);
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.ability, null);
			CBehavior newBehavior = (CBehavior) localStore.get(ABLocalStoreKeys.NEWBEHAVIOR);
			if (this.equals(newBehavior)) {
			}
			localStore.remove(ABLocalStoreKeys.PREVIOUSBEHAVIOR);
			if (newBehavior != null) {
				localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
				cleanupInputs();
				return newBehavior;
			}
			cleanupInputs();
			return this.unit.pollNextOrderBehavior(game);
		}
		return this;
	}

	private void cleanupInputs() {
		if (this.ability instanceof AbilityBuilderActiveAbility) {
			((AbilityBuilderActiveAbility) this.ability).cleanupInputs(this.actions.getCastId());
		}
	}

	@Override
	public void begin(CSimulation game) {
	}

	@Override
	public void end(CSimulation game, boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return visibleOrderId;
	}

	@Override
	public boolean interruptable() {
		return false;
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
