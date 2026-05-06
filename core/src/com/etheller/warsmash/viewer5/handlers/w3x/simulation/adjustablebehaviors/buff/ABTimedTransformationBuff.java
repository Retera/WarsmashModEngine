package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorFinishTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABOrderStartTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABDelayInstantTransformationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABTimedTransformationBuff extends ABGenericTimedBuff {

	private OnTransformationActions actions;
	private ABAbilityBuilderAbility abil;
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

	public ABTimedTransformationBuff(int handleId, ABLocalDataStore localStore, CAbility sourceAbility, CUnit sourceUnit,
			OnTransformationActions actions, War3ID alias, float duration, ABAbilityBuilderAbility ability,
			CUnitType newType, final boolean keepRatios, boolean addAlternateTagAfter, boolean permanent,
			float transformationDuration, float transformationTime, float landingDelay, float altitudeAdjustmentDelay,
			float altitudeAdjustmentDuration, boolean immediateLanding, boolean immediateTakeoff) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, true, false, true, false);
		this.setIconShowing(false);
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

	public ABTimedTransformationBuff(int handleId, ABLocalDataStore localStore, CAbility sourceAbility, CUnit sourceUnit,
			OnTransformationActions actions, War3ID alias, float duration, ABAbilityBuilderAbility ability,
			CUnitType newType, boolean addAlternateTagAfter, boolean permanent, float transformationDuration) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, true, false, true, false);
		this.setIconShowing(false);
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
			ABTransformationHandler.instantTransformation(localStore, unit, targetType, keepRatios, actions, abil,
					addAlternateTagAfter, perm, true);
			unit.remove(game, this);
		}
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		int visibleOrderId = -1;
		int transformId = -1;
		if (abil instanceof ABAbilityBuilderActiveAbility) {
			ABAbilityBuilderActiveAbility actabil = (ABAbilityBuilderActiveAbility) abil;
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
				ABTransformationHandler.playMorphAnimation(unit, addAlternateTagAfter);
				new ABDelayInstantTransformationTimer(sourceUnit, localStore, unit, actions, addAlternateTagAfter,
						transTime, null, targetType, keepRatios, abil, null, transTime, 0).start(game);
			} else {
				ABTransformationHandler.instantTransformation(localStore, unit, targetType, keepRatios, actions, abil,
						addAlternateTagAfter, perm, true);
			}
			unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this.abil, null);
			unit.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this.abil, null);
		} else {
			this.localStore.put(ABLocalStoreKeys.PREVIOUSBEHAVIOR, unit.getCurrentBehavior());
			unit.order(game, new ABOrderStartTransformation(
					new ABBehaviorFinishTransformation(sourceUnit, localStore, unit, abil, targetType, keepRatios,
							actions, addAlternateTagAfter, visibleOrderId, perm, dur, transTime, landTime, atlAdDelay,
							altAdTime, imLand, imTakeOff, this.getAlias(), targetType, instantTransformation),
					transformId), false);
		}
	}

}
