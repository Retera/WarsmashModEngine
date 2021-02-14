package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.awt.image.BufferedImage;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.CBehaviorOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CAbilityOrcBuild extends AbstractCAbilityBuild {
	private CBehaviorOrcBuild buildBehavior;

	public CAbilityOrcBuild(final int handleId, final List<War3ID> structuresBuilt) {
		super(handleId, structuresBuilt);
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.buildBehavior = new CBehaviorOrcBuild(unit);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		final CUnitType unitType = game.getUnitData().getUnitType(orderIdAsRawtype);
		final BufferedImage buildingPathingPixelMap = unitType.getBuildingPathingPixelMap();
		if (buildingPathingPixelMap != null) {
			point.x = (float) Math.floor(point.x / 64f) * 64f;
			point.y = (float) Math.floor(point.y / 64f) * 64f;
			if (((buildingPathingPixelMap.getWidth() / 2) % 2) == 1) {
				point.x += 32f;
			}
			if (((buildingPathingPixelMap.getHeight() / 2) % 2) == 1) {
				point.y += 32f;
			}
		}
		final CPlayer player = game.getPlayer(caster.getPlayerIndex());
		player.chargeFor(unitType);
		if (unitType.getFoodUsed() != 0) {
			player.setFoodUsed(player.getFoodUsed() + unitType.getFoodUsed());
		}
		return this.buildBehavior.reset(point, orderId, getBaseOrderId());
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.orcbuild;
	}
}
