package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABDelayInstantTransformationTimer extends CTimer {
	private CUnit sourceUnit;
	private ABLocalDataStore localStore;
	private OnTransformationActions actions;
	private CUnit unit;
	private boolean addAlternateTagAfter;
	private CUnitType baseType;
	private CUnitType targetType;
	private boolean keepRatios;
	private ABAbilityBuilderAbility abil;
	private boolean perm;
	private War3ID theBuffId;
	private float transTime;
	private float dur;

	public ABDelayInstantTransformationTimer(CUnit sourceUnit, ABLocalDataStore localStore, CUnit unit,
			OnTransformationActions actions, boolean addAlternateTagAfter, float delay, CUnitType baseType,
			CUnitType targetType, final boolean keepRatios, ABAbilityBuilderAbility ability, War3ID buffId,
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
		ABTransformationHandler.instantTransformation(localStore, unit, targetType, keepRatios, actions, abil,
				addAlternateTagAfter, perm, false);
		if (dur > 0) {
			ABTransformationHandler.createInstantTransformBackBuff(sourceUnit, localStore, unit, baseType, keepRatios,
					actions.createUntransformActions(), abil, theBuffId, addAlternateTagAfter, transTime, dur, perm);
		}
	}

}
