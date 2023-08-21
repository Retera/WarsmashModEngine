package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public final class ABAbilityTargetStillAliveAndTargetableVisitor implements AbilityTargetVisitor<Boolean> {
	private CSimulation simulation;
	private CUnit unit;
	private AbilityBuilderAbility ability;

	public ABAbilityTargetStillAliveAndTargetableVisitor reset(final CSimulation simulation, final CUnit unit,
			final AbilityBuilderAbility ability) {
		this.simulation = simulation;
		this.unit = unit;
		this.ability = ability;
		return this;
	}

	@Override
	public Boolean accept(final AbilityPointTarget target) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean accept(final CUnit target) {
		BooleanAbilityTargetCheckReceiver<CWidget> receiver = new BooleanAbilityTargetCheckReceiver<>();
		ability.checkCanTarget(simulation, unit, ability.getBaseOrderId(), target, receiver);
		return !target.isHidden()
				&& receiver.isTargetable();
	}

	@Override
	public Boolean accept(final CDestructable target) {
		BooleanAbilityTargetCheckReceiver<CWidget> receiver = new BooleanAbilityTargetCheckReceiver<>();
		ability.checkCanTarget(simulation, unit, ability.getBaseOrderId(), target, receiver);
		return !target.isDead() && receiver.isTargetable();
	}

	@Override
	public Boolean accept(final CItem target) {
		BooleanAbilityTargetCheckReceiver<CWidget> receiver = new BooleanAbilityTargetCheckReceiver<>();
		ability.checkCanTarget(simulation, unit, ability.getBaseOrderId(), target, receiver);
		return !target.isDead() && !target.isHidden()
				&& receiver.isTargetable();
	}

}