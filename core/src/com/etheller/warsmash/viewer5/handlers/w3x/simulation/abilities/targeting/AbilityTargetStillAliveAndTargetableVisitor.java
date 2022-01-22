package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public final class AbilityTargetStillAliveAndTargetableVisitor implements AbilityTargetVisitor<Boolean> {
	private CSimulation simulation;
	private CUnit unit;
	private EnumSet<CTargetType> targetsAllowed;

	public AbilityTargetStillAliveAndTargetableVisitor reset(final CSimulation simulation, final CUnit unit,
			final EnumSet<CTargetType> targetsAllowed) {
		this.simulation = simulation;
		this.unit = unit;
		this.targetsAllowed = targetsAllowed;
		return this;
	}

	@Override
	public Boolean accept(final AbilityPointTarget target) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean accept(final CUnit target) {
		return !target.isDead() && !target.isHidden()
				&& target.canBeTargetedBy(this.simulation, this.unit, this.targetsAllowed);
	}

	@Override
	public Boolean accept(final CDestructable target) {
		return !target.isDead() && target.canBeTargetedBy(this.simulation, this.unit, this.targetsAllowed);
	}

	@Override
	public Boolean accept(final CItem target) {
		return !target.isDead() && !target.isHidden()
				&& target.canBeTargetedBy(this.simulation, this.unit, this.targetsAllowed);
	}

}