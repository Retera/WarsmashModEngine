package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class DelayInstantTransformationTimer extends CTimer {
	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private CUnit unit;
	private boolean addAlternateTagAfter;
	private CUnitType baseType;
	private CUnitType targetType;
	private AbilityBuilderAbility abil;
	private boolean perm;
	private War3ID theBuffId;
	private float transTime;
	private float dur;

	public DelayInstantTransformationTimer(CSimulation game, Map<String, Object> localStore, CUnit unit,
			OnTransformationActions actions, boolean addAlternateTagAfter, float delay, CUnitType baseType,
			CUnitType targetType, AbilityBuilderAbility ability, War3ID buffId, float transformationTime,
			float duration) {
		super();
		this.localStore = localStore;
		this.unit = unit;
		this.actions = actions;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.baseType = baseType;
		this.targetType = targetType;
		this.abil = ability;
		this.theBuffId = buffId;
		this.transTime = transformationTime;
		this.dur = duration;
		this.setRepeats(false);
		this.setTimeoutTime(delay);
	}

	public void onFire(CSimulation game) {
		TransformationHandler.instantTransformation(game, localStore, unit, targetType, actions, abil,
				addAlternateTagAfter, perm, false);
		if (dur > 0) {
			TransformationHandler.createInstantTransformBackBuff(game, localStore, unit, baseType,
					actions.createUntransformActions(), abil, theBuffId,
					addAlternateTagAfter, transTime, dur, perm);
		}
	}

}
