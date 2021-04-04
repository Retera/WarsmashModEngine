package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityHumanBuild extends AbstractCAbilityBuild {

	public CAbilityHumanBuild(final int handleId, final List<War3ID> structuresBuilt) {
		super(handleId, structuresBuilt);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.humanbuild;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
//		caster.getMoveBehavior().reset(point.x, point.y, )
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
		// TODO Auto-generated method stub

	}

}
