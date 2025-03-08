package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CBehaviorReturnResources extends CAbstractRangedBehavior implements AbilityTargetVisitor<CBehavior> {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;

	public CBehaviorReturnResources(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit);
		this.abilityHarvest = abilityHarvest;
	}

	public CBehavior reset(final CSimulation simulation) {
		final CUnit nearestDropoffPoint = findNearestDropoffPoint(simulation);
		if (nearestDropoffPoint == null) {
			// TODO it is unconventional not to return self here
			return this.unit.pollNextOrderBehavior(simulation);
		}
		return innerReset(simulation, nearestDropoffPoint, true);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		// TODO this is probably not what the CloseEnoughRange constant is for
		return this.unit.canReach(this.target, this.unit.getUnitType().getCollisionSize());
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.returnresources;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.simulation = simulation;
		return this.target.visit(this);
	}

	@Override
	public CBehavior accept(final AbilityPointTarget target) {
		return CBehaviorReturnResources.this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	public CBehavior accept(final CUnit target) {
		for (final CAbility ability : target.getAbilities()) {
			if (ability instanceof CAbilityReturnResources) {
				final CAbilityReturnResources abilityReturnResources = (CAbilityReturnResources) ability;
				if (abilityReturnResources.accepts(this.abilityHarvest.getCarriedResourceType())) {
					final CPlayer player = this.simulation.getPlayer(this.unit.getPlayerIndex());
					CWidget nextTarget = null;
					switch (this.abilityHarvest.getCarriedResourceType()) {
					case FOOD:
						throw new IllegalStateException("Unit used Harvest skill to carry FOOD resource!");
					case GOLD:
						player.setGold(player.getGold() + this.abilityHarvest.getCarriedResourceAmount());
						if (this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.GOLD)) {
							this.unit.getUnitAnimationListener().forceResetCurrentAnimation();
						}
						if ((this.abilityHarvest.getLastHarvestTarget() != null) && this.abilityHarvest
								.getLastHarvestTarget().visit(AbilityTargetStillAliveVisitor.INSTANCE)) {
							nextTarget = this.abilityHarvest.getLastHarvestTarget();
						}
						else {
							nextTarget = findNearestMine(this.unit, this.simulation);
						}
						break;
					case LUMBER:
						player.setLumber(player.getLumber() + this.abilityHarvest.getCarriedResourceAmount());
						if (this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.LUMBER)) {
							this.unit.getUnitAnimationListener().forceResetCurrentAnimation();
						}
						if (this.abilityHarvest.getLastHarvestTarget() != null) {
							if (this.abilityHarvest.getLastHarvestTarget()
									.visit(AbilityTargetStillAliveVisitor.INSTANCE)) {
								nextTarget = this.abilityHarvest.getLastHarvestTarget();
							}
							else {
								nextTarget = findNearestTree(this.unit, this.abilityHarvest, this.simulation,
										this.abilityHarvest.getLastHarvestTarget());
							}
						}
						else {
							nextTarget = findNearestTree(this.unit, this.abilityHarvest, this.simulation, this.unit);
						}
						break;
					}
					this.simulation.unitGainResourceEvent(this.unit, player.getId(),
							this.abilityHarvest.getCarriedResourceType(),
							this.abilityHarvest.getCarriedResourceAmount());
					this.abilityHarvest.setCarriedResources(this.abilityHarvest.getCarriedResourceType(), 0);
					if (nextTarget != null) {
						return this.abilityHarvest.getBehaviorHarvest().reset(this.simulation, nextTarget);
					}
					return this.unit.pollNextOrderBehavior(this.simulation);
				}
			}
		}
		return this;
	}

	@Override
	public CBehavior accept(final CDestructable target) {
		// TODO cut trees!
		return this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	public CBehavior accept(final CItem target) {
		return this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		final CUnit nearestDropoff = findNearestDropoffPoint(simulation);
		if (nearestDropoff != null) {
			this.target = nearestDropoff;
			return this;
		}
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	public CUnit findNearestDropoffPoint(final CSimulation simulation) {
		CUnit nearestDropoffPoint = null;
		double nearestDropoffDistance = Float.MAX_VALUE;
		for (final CUnit unit : simulation.getUnits()) {
			if (unit.getPlayerIndex() == this.unit.getPlayerIndex()) {
				if (unit.visit(AbilityTargetStillAliveVisitor.INSTANCE)) {
					boolean acceptedUnit = false;
					for (final CAbility ability : unit.getAbilities()) {
						if (ability instanceof CAbilityReturnResources) {
							final CAbilityReturnResources abilityReturnResources = (CAbilityReturnResources) ability;
							if (abilityReturnResources.accepts(this.abilityHarvest.getCarriedResourceType())) {
								acceptedUnit = true;
								break;
							}
						}
					}
					if (acceptedUnit) {
						// TODO maybe use distance squared, problem is that we're using this
						// inefficient more complex distance function on unit
						final double distance = unit.distanceSquaredNoCollision(this.unit);
						if (distance < nearestDropoffDistance) {
							nearestDropoffDistance = distance;
							nearestDropoffPoint = unit;
						}
					}
				}
			}
		}
		return nearestDropoffPoint;
	}

	public static CUnit findNearestMine(final CUnit worker, final CSimulation simulation) {
		CUnit nearestMine = null;
		double nearestMineDistance = Float.MAX_VALUE;
		for (final CUnit unit : simulation.getUnits()) {
			boolean acceptedUnit = false;
			for (final CAbility ability : unit.getAbilities()) {
				if (ability instanceof CAbilityGoldMine) {
					acceptedUnit = true;
					break;
				}
			}
			if (acceptedUnit) {
				// TODO maybe use distance squared, problem is that we're using this
				// inefficient more complex distance function on unit
				final double distance = unit.distanceSquaredNoCollision(worker);
				if (distance < nearestMineDistance) {
					nearestMineDistance = distance;
					nearestMine = unit;
				}
			}
		}
		return nearestMine;
	}

	public static CDestructable findNearestTree(final CUnit worker, final CAbilityHarvest abilityHarvest,
			final CSimulation simulation, final CWidget toObject) {
		CDestructable nearestMine = null;
		double nearestMineDistance = Float.MAX_VALUE;
		for (final CDestructable unit : simulation.getDestructables()) {
			if (!unit.isDead()
					&& unit.canBeTargetedBy(simulation, worker, abilityHarvest.getTreeAttack().getTargetsAllowed())) {
				// TODO maybe use distance squared, problem is that we're using this
				// inefficient more complex distance function on unit
				final double distance = unit.distanceSquaredNoCollision(toObject);
				if (distance < nearestMineDistance) {
					nearestMineDistance = distance;
					nearestMine = unit;
				}
			}
		}
		return nearestMine;
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
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}

}
