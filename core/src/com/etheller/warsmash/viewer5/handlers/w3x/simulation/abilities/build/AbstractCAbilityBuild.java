package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.util.Collection;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.menu.CAbilityMenu;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public abstract class AbstractCAbilityBuild extends AbstractCAbility implements CAbilityMenu {
	private final Set<War3ID> structuresBuilt;

	public AbstractCAbilityBuild(final int handleId, final List<War3ID> structuresBuilt) {
		super(handleId);
		this.structuresBuilt = new LinkedHashSet<>(structuresBuilt);
	}

	public Collection<War3ID> getStructuresBuilt() {
		return this.structuresBuilt;
	}

	@Override
	public void checkCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final CUnitType unitType = game.getUnitData().getUnitType(new War3ID(orderId));
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

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final Vector2 target,
			final AbilityTargetCheckReceiver<Vector2> receiver) {
		if (this.structuresBuilt.contains(new War3ID(orderId))) {
			receiver.targetOk(target);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId) {
		return false;
	}
}
