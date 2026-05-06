package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABBehaviorFinishTransformation implements CBehavior {
	private CUnit sourceUnit;
	private ABLocalDataStore localStore;
	private OnTransformationActions actions;
	private CUnit unit;
	private ABAbilityBuilderAbility ability;
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

	public ABBehaviorFinishTransformation(CUnit sourceUnit, ABLocalDataStore localStore, final CUnit unit,
			ABAbilityBuilderAbility ability, CUnitType newType, final boolean keepRatios, OnTransformationActions actions,
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
			ABTransformationHandler.startSlowTransformation(localStore, unit, newType, keepRatios, actions, ability,
					addAlternateTagAfter, takingOff, landing, immediateTakeoff, immediateLanding,
					altitudeAdjustmentDelay, landingDelay, altitudeAdjustmentDuration);
		}

		final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
		if (ticksSinceCast >= this.transformationTickDuration) {
			ABTransformationHandler.finishSlowTransformation(localStore, unit, newType, keepRatios, actions, ability,
					addAlternateTagAfter, permanent, takingOff);

			if (instantTransformAtDurationEnd) {
				ABTransformationHandler.createInstantTransformBackBuff(sourceUnit, localStore, unit, baseTypeForDuration,
						keepRatios, actions.createUntransformActions(), ability, buffId, addAlternateTagAfter,
						transformationTime, duration, permanent);
			} else {
				ABTransformationHandler.createSlowTransformBackBuff(sourceUnit, localStore, unit, baseTypeForDuration,
						keepRatios, actions.createUntransformActions(), ability, buffId, addAlternateTagAfter,
						transformationTime, duration, permanent, takingOff, landing, immediateTakeoff, immediateLanding,
						altitudeAdjustmentDelay, landingDelay, altitudeAdjustmentDuration);
			}

			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.ability, null);
			this.unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.ability, null);
			CBehavior newBehavior = localStore.get(ABLocalStoreKeys.NEWBEHAVIOR, CBehavior.class);
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
		if (this.ability instanceof ABAbilityBuilderActiveAbility) {
			((ABAbilityBuilderActiveAbility) this.ability).cleanupInputs(this.actions.getCastId());
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
