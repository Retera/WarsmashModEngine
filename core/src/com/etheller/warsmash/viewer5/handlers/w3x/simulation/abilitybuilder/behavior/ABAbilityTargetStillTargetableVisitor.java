package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public final class ABAbilityTargetStillTargetableVisitor implements AbilityTargetVisitor<Boolean> {
	private CSimulation simulation;
	private CUnit unit;
	private AbilityBuilderActiveAbility ability;
	private boolean autoOrder;
	private boolean channeling;
	private int playerIndex;
	private int orderId;

	public ABAbilityTargetStillTargetableVisitor reset(final CSimulation simulation, final CUnit unit,
			final AbilityBuilderActiveAbility ability, boolean autoOrder, boolean channeling) {
		this.simulation = simulation;
		this.unit = unit;
		this.ability = ability;
		this.autoOrder = autoOrder;
		this.channeling = channeling;
		this.playerIndex = unit.getPlayerIndex();
		this.orderId = this.ability.getBaseOrderId();
		return this;
	}

	public ABAbilityTargetStillTargetableVisitor reset(final CSimulation simulation, final CUnit unit,
			final AbilityBuilderActiveAbility ability, boolean autoOrder, boolean channeling, final int playerIndex, int orderId) {
		this.simulation = simulation;
		this.unit = unit;
		this.ability = ability;
		this.autoOrder = autoOrder;
		this.channeling = channeling;
		this.playerIndex = playerIndex;
		this.orderId = orderId;
		return this;
	}

	@Override
	public Boolean accept(final AbilityPointTarget target) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean accept(final CUnit target) {
		if (this.channeling) {
			return !target.isHidden();
		}
		BooleanAbilityTargetCheckReceiver<CWidget> receiver = new BooleanAbilityTargetCheckReceiver<>();
		ability.checkCanTarget(simulation, unit, this.playerIndex, this.orderId, this.autoOrder, target, receiver);
		return !target.isHidden() && receiver.isTargetable();
	}

	@Override
	public Boolean accept(final CDestructable target) {
		if (this.channeling) {
			return !target.isDead();
		}
		BooleanAbilityTargetCheckReceiver<CWidget> receiver = new BooleanAbilityTargetCheckReceiver<>();
		ability.checkCanTarget(simulation, unit, this.playerIndex, this.orderId, this.autoOrder, target, receiver);
		return !target.isDead() && receiver.isTargetable();
	}

	@Override
	public Boolean accept(final CItem target) {
		if (this.channeling) {
			return !target.isDead() && !target.isHidden();
		}
		BooleanAbilityTargetCheckReceiver<CWidget> receiver = new BooleanAbilityTargetCheckReceiver<>();
		ability.checkCanTarget(simulation, unit, this.playerIndex, this.orderId, this.autoOrder, target, receiver);
		return !target.isDead() && !target.isHidden() && receiver.isTargetable();
	}

}