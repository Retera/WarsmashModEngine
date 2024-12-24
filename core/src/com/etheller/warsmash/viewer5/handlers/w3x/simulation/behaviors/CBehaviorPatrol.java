package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;

public class CBehaviorPatrol implements CRangedBehavior {

	private final CUnit unit;
	private AbilityPointTarget target;
	private AbilityPointTarget startPoint;
	private List<AbilityTarget> targets = new ArrayList<>();
	private int iter = 1;
	private boolean justAutoAttacked = false;

	public CBehaviorPatrol(final CUnit unit) {
		this.unit = unit;
	}

	public CBehavior reset(final AbilityPointTarget target) {
		targets.clear();
		this.target = target;
		this.startPoint = new AbilityPointTarget(this.unit.getX(), this.unit.getY());
		targets.add(this.startPoint);
		targets.add(target);
		iter = 1;
		return this;
	}
	
	public void addPatrolPoint(final AbilityTarget target) {
		CItem tarItem = target.visit(AbilityTargetVisitor.ITEM);
		if (tarItem != null) {
			targets.add(new AbilityPointTarget(tarItem.getX(), tarItem.getY()));
		} else {
			CDestructable tarDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			if (tarDest != null) {
				targets.add(new AbilityPointTarget(tarDest.getX(), tarDest.getY()));
			} else {
				targets.add(target);
			}
		}
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.patrol;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		if (this.justAutoAttacked = this.unit.autoAcquireTargets(simulation, false)) {
			// kind of a hack
			return true;
		}
		return this.unit.distance(this.target.x, this.target.y) <= 16f; // TODO this is not how it was meant to be used
	}

	@Override
	public CBehavior update(final CSimulation simulation) {
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			return this.unit.getCurrentBehavior();
		}
		
		iter++;
		if (iter >= this.targets.size()) {
			iter = 0;
		}
		
		CUnit tarUnit = this.targets.get(iter).visit(AbilityTargetVisitor.UNIT);
		if (tarUnit != null) {
			if (simulation.getPlayer(unit.getPlayerIndex()).hasAlliance(tarUnit.getPlayerIndex(), CAllianceType.PASSIVE)) {
				unit.getOrderQueue().clear();
				return unit.getFollowBehavior().reset(simulation, this.getHighlightOrderId(), tarUnit);
			} else {
				AbilityPointTarget newTar = new AbilityPointTarget(tarUnit.getX(), tarUnit.getY());
				this.targets.set(iter, newTar);
				this.target = newTar;
			}
		} else {
			AbilityPointTarget tarPoint = this.targets.get(iter).visit(AbilityTargetVisitor.POINT);
			if (tarPoint != null) {
				this.target = tarPoint;
			}
		}
		
		return this.unit.getMoveBehavior().reset(this.target, this, false);
	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}


	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public AbilityTarget getTarget() {
		return this.target;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}
}
