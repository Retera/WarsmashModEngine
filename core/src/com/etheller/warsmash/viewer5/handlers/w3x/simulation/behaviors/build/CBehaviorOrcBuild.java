package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedPointTargetBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class CBehaviorOrcBuild extends CAbstractRangedPointTargetBehavior {
	private int highlightOrderId;
	private War3ID orderId;

	public CBehaviorOrcBuild(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(final float targetX, final float targetY, final int orderId, final int highlightOrderId) {
		this.highlightOrderId = highlightOrderId;
		this.orderId = new War3ID(orderId);
		return innerReset(targetX, targetY);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		final CUnitType unitType = simulation.getUnitData().getUnitType(this.orderId);
		return this.unit.canReachToPathing(0, simulation.getGameplayConstants().getBuildingAngle(),
				unitType.getBuildingPathingPixelMap(), this.targetX, this.targetY);
	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinRange) {
		final CUnit constructedStructure = simulation.createUnit(this.orderId, this.unit.getPlayerIndex(), this.targetX,
				this.targetY, simulation.getGameplayConstants().getBuildingAngle());
		constructedStructure.setConstructing(true);
		constructedStructure.setWorkerInside(this.unit);
		this.unit.setHidden(true);
		this.unit.setUpdating(false);
		simulation.unitConstructedEvent(this.unit, constructedStructure);
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

}
