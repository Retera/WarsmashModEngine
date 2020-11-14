package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public final class CAbilityQueue extends AbstractCAbility {
	private final Set<War3ID> unitsTrained;
	private final Set<War3ID> researchesAvailable;

	public CAbilityQueue(final int handleId, final List<War3ID> unitsTrained, final List<War3ID> researchesAvailable) {
		super(handleId);
		this.unitsTrained = new LinkedHashSet<>(unitsTrained);
		this.researchesAvailable = new LinkedHashSet<>(researchesAvailable);
	}

	public Set<War3ID> getUnitsTrained() {
		return this.unitsTrained;
	}

	public Set<War3ID> getResearchesAvailable() {
		return this.researchesAvailable;
	}

	@Override
	public void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		if (this.unitsTrained.contains(orderIdAsRawtype) || this.researchesAvailable.contains(orderIdAsRawtype)) {
			final CUnitType unitType = game.getUnitData().getUnitType(orderIdAsRawtype);
			if (unitType != null) {
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				if (player.getGold() >= unitType.getGoldCost()) {
					if (player.getLumber() >= unitType.getLumberCost()) {
						receiver.useOk();
					}
					else {
						receiver.notEnoughResources(ResourceType.LUMBER);
					}
				}
				else {
					receiver.notEnoughResources(ResourceType.GOLD);
				}
			}
			else {
				receiver.useOk();
			}
		}
		else {
			/// ???
			receiver.useOk();
		}
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if (this.unitsTrained.contains(new War3ID(orderId)) || this.researchesAvailable.contains(new War3ID(orderId))) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId) {
		return true;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final Vector2 point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		final War3ID rawcode = new War3ID(orderId);
		if (this.unitsTrained.contains(rawcode)) {
			caster.queueTrainingUnit(rawcode);
		}
		else if (this.researchesAvailable.contains(rawcode)) {
			caster.queueResearch(rawcode);
		}
		return null;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
