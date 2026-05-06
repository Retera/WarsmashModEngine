package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.worker;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionDisableWorkerAbilities implements ABSingleAction {

	private ABUnitCallback unit;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit targetUnit = caster;
		if (this.unit != null) {
			targetUnit = this.unit.callback(caster, localStore, castId);
		}

		final Class[] workerAbils = { CAbilityAcolyteHarvest.class, CAbilityHarvest.class, CAbilityWispHarvest.class,
				CAbilityHumanBuild.class, CAbilityHumanRepair.class, CAbilityNagaBuild.class,
				CAbilityNeutralBuild.class, CAbilityNightElfBuild.class, CAbilityOrcBuild.class, CAbilityRepair.class,
				CAbilityUndeadBuild.class };
		for (final Class type : workerAbils) {
			final CAbility abil = targetUnit.getFirstAbilityOfType(type);
			if (abil != null) {
				abil.setDisabled(true, CAbilityDisableType.TRANSFORMATION);
				abil.setIconShowing(false);
				targetUnit.checkDisabledAbilities(localStore.game, true);
			}
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String casterExpression;
		if (this.unit != null) {
			casterExpression = this.unit.generateJassEquivalent(jassTextGenerator);
		}
		else {
			casterExpression = jassTextGenerator.getCaster();
		}

		return "DisableWorkerAbilitiesAU(" + casterExpression + ", true)";
	}

}
