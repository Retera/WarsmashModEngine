package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;

public class ABGenericAuraBuff extends ABBuff {
	private Map<Integer, NonStackingFx > fx;
	
	private CUnit caster;

	public ABGenericAuraBuff(int handleId, War3ID alias, CUnit caster) {
		super(handleId, alias, alias);
		this.caster = caster;
		this.fx = new HashMap<>();
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		if (unit != this.caster) {
			this.fx.put(unit.getHandleId(), unit.addNonStackingFx(game, "aura", getAlias(), CEffectType.TARGET));
		}
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		NonStackingFx theFx = this.fx.get(unit.getHandleId());
		if (theFx != null) {
			this.fx.remove(unit.getHandleId());
			unit.removeNonStackingFx(game, theFx);
		}
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
