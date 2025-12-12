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
	private CUnit sourceUnit;
	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private CUnit unit;
	private boolean addAlternateTagAfter;
	private CUnitType baseType;
	private CUnitType targetType;
	private boolean keepRatios;
	private AbilityBuilderAbility abil;
	private boolean perm;
	private War3ID theBuffId;
	private float transTime;
	private float dur;

	public DelayInstantTransformationTimer(CSimulation game, CUnit sourceUnit, Map<String, Object> localStore, CUnit unit,
			OnTransformationActions actions, boolean addAlternateTagAfter, float delay, CUnitType baseType,
			CUnitType targetType, final boolean keepRatios, AbilityBuilderAbility ability, War3ID buffId,
			float transformationTime, float duration) {
		super();
		this.sourceUnit = sourceUnit;
		this.localStore = localStore;
		this.unit = unit;
		this.actions = actions;
		this.addAlternateTagAfter = addAlternateTagAfter;
		this.baseType = baseType;
		this.targetType = targetType;
		this.keepRatios = keepRatios;
		this.abil = ability;
		this.theBuffId = buffId;
		this.transTime = transformationTime;
		this.dur = duration;
		this.setRepeats(false);
		this.setTimeoutTime(delay);
	}

	public void onFire(CSimulation game) {
		TransformationHandler.instantTransformation(game, localStore, unit, targetType, keepRatios, actions, abil,
				addAlternateTagAfter, perm, false);
		if (dur > 0) {
			TransformationHandler.createInstantTransformBackBuff(game, sourceUnit, localStore, unit, baseType, keepRatios,
					actions.createUntransformActions(), abil, theBuffId, addAlternateTagAfter, transTime, dur, perm);
		}
	}

}
