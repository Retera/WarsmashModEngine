package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABDelayInstantTransformationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABTimedInstantTransformationBuff extends ABGenericTimedBuff {

	private OnTransformationActions actions;
	private ABAbilityBuilderPassiveAbility abil;
	private CUnitType targetType;
	private boolean keepRatios;
	private boolean addAlternateTagAfter;
	private boolean perm;
	private float dur;
	private float transTime;

	public ABTimedInstantTransformationBuff(int handleId, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, OnTransformationActions actions, War3ID alias, float duration,
			ABAbilityBuilderPassiveAbility ability, CUnitType newType, final boolean keepRatios,
			boolean addAlternateTagAfter, boolean permanent, float transformationDuration) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, true, false, true, false);
		this.setIconShowing(false);
		this.actions = actions;
		this.abil = ability;
		this.targetType = newType;
		this.keepRatios = keepRatios;
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
	public void onDeath(CSimulation game, CUnit unit) {
		if (unit.isHero()) {
			ABTransformationHandler.instantTransformation(localStore, unit, targetType, keepRatios, actions, abil,
					addAlternateTagAfter, perm, true);
			unit.remove(game, this);
		}
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
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
	}

}
