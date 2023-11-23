package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.DelayInstantTransformationTimer;

public class ABTimedInstantTransformationBuff extends ABGenericTimedBuff {

	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private AbilityBuilderPassiveAbility abil;
	private CUnitType targetType;
	private boolean addAlternateTagAfter;
	private boolean perm;
	private float dur;
	private float transTime;

	public ABTimedInstantTransformationBuff(int handleId, Map<String, Object> localStore, OnTransformationActions actions, War3ID alias, float duration, AbilityBuilderPassiveAbility ability, 
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
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		if (dur > 0) {
			TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
			new DelayInstantTransformationTimer(game, localStore, unit, actions, addAlternateTagAfter, transTime, null, targetType, abil,
					null, transTime, 0).start(game);
		} else {
			TransformationHandler.instantTransformation(game, localStore, unit, targetType, actions, abil, addAlternateTagAfter, perm, true);
		}
	}

}
