package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public abstract class CAbilityPointTargetSpellBase extends CAbilitySpellBase {
	private CBehaviorTargetSpellBase behavior;

	public CAbilityPointTargetSpellBase(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behavior = new CBehaviorTargetSpellBase(unit, this);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return this.behavior.reset(point);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
			receiver.targetOk(target);
		}
		else {
			receiver.targetOutsideRange();
		}
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

}
