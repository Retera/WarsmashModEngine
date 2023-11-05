package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityDisableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionDisableWorkerAbilities implements ABAction {

	private ABUnitCallback unit;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit targetUnit = caster;
		if (unit != null) {
			targetUnit = unit.callback(game, caster, localStore, castId);
		}

		Class[] workerAbils = { CAbilityAcolyteHarvest.class, CAbilityHarvest.class, CAbilityWispHarvest.class,
				CAbilityHumanBuild.class, CAbilityHumanRepair.class, CAbilityNagaBuild.class,
				CAbilityNeutralBuild.class, CAbilityNightElfBuild.class, CAbilityOrcBuild.class, CAbilityRepair.class,
				CAbilityUndeadBuild.class };
		for (Class type : workerAbils) {
			CAbility abil = targetUnit.getFirstAbilityOfType(type);
			if (abil != null) {
				abil.setDisabled(true, CAbilityDisableType.TRANSFORMATION);
				abil.setIconShowing(false);
				targetUnit.checkDisabledAbilities(game, true);
			}
		}
	}

}
