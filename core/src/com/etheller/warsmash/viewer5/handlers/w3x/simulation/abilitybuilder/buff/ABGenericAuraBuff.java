package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABGenericAuraBuff extends AbstractCBuff {
	private Map<Integer, SimulationRenderComponent > fx;
	
	private CUnit caster;
	private War3ID casterArt;

	public ABGenericAuraBuff(int handleId, War3ID alias, CUnit caster, War3ID casterArt) {
		super(handleId, alias);
		this.caster = caster;
		this.casterArt = casterArt;
		this.fx = new HashMap<>();
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		if (unit == this.caster) {
			this.fx.put(unit.getHandleId(), game.createSpellEffectOnUnit(unit, this.casterArt, CEffectType.TARGET, 0));
		} else {
			this.fx.put(unit.getHandleId(), game.createSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET, 0));
		}
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		SimulationRenderComponent theFx = this.fx.get(unit.getHandleId());
		if (theFx != null) {
			this.fx.remove(unit.getHandleId());
			theFx.remove();
		}
	}

	@Override
	public float getDurationRemaining(CSimulation game) {
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

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
	}

	@Override
	public void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
	}

}
