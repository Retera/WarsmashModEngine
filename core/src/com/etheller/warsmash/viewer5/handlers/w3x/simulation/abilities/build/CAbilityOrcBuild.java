package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.CBehaviorOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

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
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final Vector2 point) {
		return this.buildBehavior.reset(point.x, point.y, orderId, getBaseOrderId());
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
