package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CBehaviorNightElfBuild extends CBehaviorOrcBuild {

	public CBehaviorNightElfBuild(final CUnit unit) {
		super(unit);
	}

	@Override
	protected void onStructureCreated(final CSimulation game, final CUnit constructedStructure,
			final CAbilityBuildInProgress abilityBuildInProgress) {
		if (constructedStructure.getClassifications().contains(CUnitClassification.ANCIENT)) {
			constructedStructure.setConstructionConsumesWorker(true);
			final CUnit worker = constructedStructure.getWorker();
			final CPlayer workerPlayer = game.getPlayer(worker.getPlayerIndex());
			workerPlayer.setUnitFoodUsed(worker, 0);
		}
	}

}
