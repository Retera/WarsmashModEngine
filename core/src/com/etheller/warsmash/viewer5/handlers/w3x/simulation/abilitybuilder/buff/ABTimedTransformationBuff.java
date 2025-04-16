package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorFinishTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.COrderStartTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.DelayInstantTransformationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABTimedTransformationBuff extends ABGenericTimedBuff {

	private Map<String, Object> localStore;
	private OnTransformationActions actions;
	private AbilityBuilderAbility abil;
	private CUnitType targetType;
	private boolean keepRatios;
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

	public ABTimedTransformationBuff(int handleId, CAbility sourceAbility, CUnit sourceUnit, Map<String, Object> localStore, OnTransformationActions actions,
			War3ID alias, float duration, AbilityBuilderAbility ability, CUnitType newType, final boolean keepRatios,
			boolean addAlternateTagAfter, boolean permanent, float transformationDuration, float transformationTime,
			float landingDelay, float altitudeAdjustmentDelay, float altitudeAdjustmentDuration,
			boolean immediateLanding, boolean immediateTakeoff) {
		super(handleId, alias, sourceAbility, sourceUnit, duration, true, false, true, false);
		this.setIconShowing(false);
		this.localStore = localStore;
		this.actions = actions;
		this.abil = ability;
		this.targetType = newType;
		this.keepRatios = keepRatios;
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

	public ABTimedTransformationBuff(int handleId, CAbility sourceAbility, CUnit sourceUnit, Map<String, Object> localStore, OnTransformationActions actions,
			War3ID alias, float duration, AbilityBuilderAbility ability, CUnitType newType,
			boolean addAlternateTagAfter, boolean permanent, float transformationDuration) {
		super(handleId, alias, sourceAbility, sourceUnit, duration, true, false, true, false);
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
	public void onDeath(CSimulation game, CUnit unit) {
		if (unit.isHero()) {
			TransformationHandler.instantTransformation(game, localStore, unit, targetType, keepRatios, actions, abil,
					addAlternateTagAfter, perm, true);
			unit.remove(game, this);
		}
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		int visibleOrderId = -1;
		int transformId = -1;
		if (abil instanceof AbilityBuilderActiveAbility) {
			AbilityBuilderActiveAbility actabil = (AbilityBuilderActiveAbility) abil;
			if (actabil.isToggleOn()) {
				actabil.deactivate(game, unit);
			}
			visibleOrderId = addAlternateTagAfter ? actabil.getBaseOrderId() : actabil.getOffOrderId();
			transformId = actabil.getBaseOrderId();
		}
		unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this.abil, null);
		unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this.abil, null);
		unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this.abil, null);
		if (instantTransformation) {
			if (dur > 0) {
				TransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
				new DelayInstantTransformationTimer(game, sourceUnit, localStore, unit, actions, addAlternateTagAfter, transTime,
						null, targetType, keepRatios, abil, null, transTime, 0).start(game);
			} else {
				TransformationHandler.instantTransformation(game, localStore, unit, targetType, keepRatios, actions,
						abil, addAlternateTagAfter, perm, true);
			}
			unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.abil, null);
			unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.abil, null);
		} else {
			this.localStore.put(ABLocalStoreKeys.PREVIOUSBEHAVIOR, unit.getCurrentBehavior());
			unit.order(game,
					new COrderStartTransformation(
							new CBehaviorFinishTransformation(sourceUnit, localStore, unit, abil, targetType, keepRatios, actions,
									addAlternateTagAfter, visibleOrderId, perm, dur, transTime, landTime, atlAdDelay,
									altAdTime, imLand, imTakeOff, this.getAlias(), targetType, instantTransformation),
							transformId),
					false);
		}
	}

}
