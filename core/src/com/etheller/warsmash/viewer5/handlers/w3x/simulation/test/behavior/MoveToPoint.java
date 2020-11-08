package com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.behavior;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.state.MoveState;

public class MoveToPoint implements IBehavior {

	private final CSimulation simulation;
	private final CUnit unit;
	private final Point point;
	private final MoveState moveState;
	private final List<Float> waypointList;

	public MoveToPoint(final CSimulation simulation, final CUnit unit, final Point point) {
		this.simulation = simulation;
		this.unit = unit;
		this.point = point;
		this.waypointList = this.simulation.findNaiveSlowPath(unit, null, unit.getX(), unit.getY(),
				new Point2D.Float(point.x, point.y), unit.getUnitType().getMovementType(),
				unit.getUnitType().getCollisionSize(), true);
		this.moveState = new MoveState();
		this.unit.setBehavior(this);
		this.unit.setState(this.moveState);
		resolveNext();
	}

	@Override
	public void resolveNext() {
		if (this.waypointList.isEmpty()) {
			this.unit.setState(state);
		}
		else {
			final Float firstWaypoint = this.waypointList.remove(0);
			this.unit.setState(this.moveState.reset(this, this.unit, firstWaypoint.x, firstWaypoint.y));
		}
	}

}
