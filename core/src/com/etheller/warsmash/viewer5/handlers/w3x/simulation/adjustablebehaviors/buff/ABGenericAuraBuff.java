package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;

public class ABGenericAuraBuff extends ABBuff {
	private Map<Integer, NonStackingFx> fx;

	private CUnit caster;

	public ABGenericAuraBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit caster, boolean leveled, boolean positive) {
		super(handleId, alias, alias, localStore, sourceAbility, caster);
		this.caster = caster;
		this.fx = new HashMap<>();
		this.setHero(caster.isHero());
		this.setLeveled(leveled);
		this.setPositive(positive);
		this.setAura(true);
		this.setLevel(null, null, 1);
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
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
	}

}
