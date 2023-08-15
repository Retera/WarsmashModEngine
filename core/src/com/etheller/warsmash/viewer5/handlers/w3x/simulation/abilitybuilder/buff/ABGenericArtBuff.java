package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABGenericArtBuff extends ABBuff {
	private SimulationRenderComponent fx;

	public ABGenericArtBuff(int handleId, War3ID alias) {
		super(handleId, alias);
		this.setIconShowing(false);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		this.fx.remove();
	}

	@Override
	public float getDurationRemaining(CSimulation game, final CUnit unit) {
		return 0;
	}

	@Override
	public float getDurationMax() {
		return 0;
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
	}

}
