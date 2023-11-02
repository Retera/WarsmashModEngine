package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorFinishTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.COrderStartTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.DelayInstantTransformationTimer;

public class ABTimedTransformationBuff extends ABGenericTimedBuff {

	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private AbilityBuilderActiveAbility abil;
	private CUnitType targetType;
	private boolean addAlternateTagAfter;
	private boolean perm;
	private float dur;
	private float transTime;
	private float landTime;
	private float atlAdDelay;
	private float altAdTime;
	private boolean imLand;
	private boolean imTakeOff;
	private boolean instantTransformation;

	public ABTimedTransformationBuff(int handleId, Map<String, Object> localStore, OnTransformationActions actions, War3ID alias, float duration, AbilityBuilderActiveAbility ability,
			CUnitType newType, boolean addAlternateTagAfter, boolean permanent, float transformationDuration,
			float transformationTime, float landingDelay, float altitudeAdjustmentDelay,
			float altitudeAdjustmentDuration, boolean immediateLanding, boolean immediateTakeoff) {
		super(handleId, alias, duration, true);
		this.setIconShowing(false);
		this.localStore = localStore;
		this.actions = actions;
		this.abil = ability;
		this.targetType = newType;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.perm = permanent;
		this.dur = transformationDuration;
		this.transTime = transformationTime;
		this.landTime = landingDelay;
		this.atlAdDelay = altitudeAdjustmentDelay;
		this.altAdTime = altitudeAdjustmentDuration;
		this.imLand = immediateLanding;
		this.imTakeOff = immediateTakeoff;
		this.instantTransformation = false;
	}

	public ABTimedTransformationBuff(int handleId, Map<String, Object> localStore, OnTransformationActions actions, War3ID alias, float duration, AbilityBuilderActiveAbility ability, 
			CUnitType newType, boolean addAlternateTagAfter, boolean permanent, float transformationDuration) {
		super(handleId, alias, duration, true);
		this.setIconShowing(false);
		this.localStore = localStore;
		this.actions = actions;
		this.abil = ability;
		this.targetType = newType;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.perm = permanent;
		this.dur = transformationDuration;
		this.instantTransformation = true;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		if (abil.isToggleOn()) {
			abil.deactivate(game, unit);
		}
		if (instantTransformation) {
			if (dur > 0) {
				TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
				new DelayInstantTransformationTimer(game, localStore, unit, actions, addAlternateTagAfter, transTime, null, targetType, abil,
						null, transTime, 0).start(game);
			} else {
				TransformationHandler.instantTransformation(game, localStore, unit, targetType, actions, abil, addAlternateTagAfter, perm, true);
			}
		} else {
			unit.order(game, new COrderStartTransformation(
					new CBehaviorFinishTransformation(localStore, unit, abil, targetType, actions, addAlternateTagAfter,
							addAlternateTagAfter ? abil.getBaseOrderId() : abil.getOffOrderId(), perm, dur, transTime,
							landTime, atlAdDelay, altAdTime, imLand, imTakeOff, this.getAlias(), targetType, instantTransformation),
					abil.getBaseOrderId()), false);
		}
	}

}
