package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ManaDepletedCheckTimer extends CTimer {
	private CUnit caster;
	private AbilityBuilderActiveAbility ability;

	public ManaDepletedCheckTimer(CUnit caster, AbilityBuilderActiveAbility ability) {
		super();
		this.caster = caster;
		this.ability = ability;
		this.setRepeats(true);
		this.setTimeoutTime(0f);
	}

	public void onFire(CSimulation game) {
		if(caster.getMana() <= 0) {
			ability.deactivate(game, caster);
		}
	}
	
}
