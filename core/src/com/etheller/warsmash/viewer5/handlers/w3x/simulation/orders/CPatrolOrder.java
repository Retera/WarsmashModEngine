package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class CPatrolOrder implements COrder {
	private final CUnit unit;
	private final int orderId;
	private final CWidget target;
	private COrder moveOrder;

	public CPatrolOrder(final CUnit unit, final int orderId, final CUnit target) {
		this.unit = unit;
		this.orderId = orderId;
		this.target = target;
		createMoveOrder(unit, target);
	}

	private void createMoveOrder(final CUnit unit, final CUnit target) {
		if (!unit.isMovementDisabled()) { // TODO: Check mobility instead
			if ((target instanceof CUnit) && !(target.getUnitType().isBuilding())) {
				this.moveOrder = new CMoveOrder(unit, this.orderId, target);
			}
			else {
				this.moveOrder = new CMoveOrder(unit, this.orderId, target.getX(), target.getY());
			}
		}
		else {
			this.moveOrder = null;
		}
	}

	@Override
	public boolean update(final CSimulation simulation) {
		if (this.target.isDead()) {
			return true;
		}
		final float range = this.unit.getAcquisitionRange();
		if (!this.unit.canReach(this.target, range)) {
			if (this.moveOrder == null) {
				return true;
			}
			if (this.moveOrder.update(simulation)) {
				return true; // we just cant reach them
			}
			return false;
		}
		return false;
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}

}
