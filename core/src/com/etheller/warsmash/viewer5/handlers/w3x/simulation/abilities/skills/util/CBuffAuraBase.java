package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
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

public abstract class CBuffAuraBase extends AbstractCBuff {
	private static final float AURA_BUFF_DECAY_TIME = 2.00f;
	private static final int AURA_BUFF_DECAY_TIME_TICKS =
			(int) (Math.ceil(AURA_BUFF_DECAY_TIME / WarsmashConstants.SIMULATION_STEP_TIME));
	private SimulationRenderComponent fx;
	private CUnit auraSourceUnit;
	private CAbilityAuraBase auraSourceAbility;
	private int nextCheckTick = 0;

	public CBuffAuraBase(int handleId, final War3ID code, War3ID alias) {
		super(handleId, code, alias);
	}

	public void setAuraSourceUnit(CUnit auraSourceUnit) {
		this.auraSourceUnit = auraSourceUnit;
	}

	public void setAuraSourceAbility(CAbilityAuraBase auraSourceAbility) {
		this.auraSourceAbility = auraSourceAbility;
	}

	protected abstract void onBuffAdd(final CSimulation game, final CUnit unit);

	protected abstract void onBuffRemove(final CSimulation game, final CUnit unit);

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		onBuffAdd(game, unit);
		this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET, 0);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		onBuffRemove(game, unit);
		this.fx.remove();
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= nextCheckTick) {
			if (!auraSourceUnit.canReach(unit, auraSourceAbility.getAreaOfEffect())) {
				unit.remove(game, this);
			}
			nextCheckTick = gameTurnTick + AURA_BUFF_DECAY_TIME_TICKS;
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		cUnit.remove(game, this);
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
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
							   AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
									   AbilityTargetCheckReceiver<Void> receiver) {
		receiver.notAnActiveAbility();
	}
	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
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
}
