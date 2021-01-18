package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CBehaviorReturnResources extends CAbstractRangedBehavior implements AbilityTargetVisitor<CBehavior> {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;

	public CBehaviorReturnResources(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit, true);
		this.abilityHarvest = abilityHarvest;
	}

	public CBehaviorReturnResources reset(final CSimulation simulation) {
		innerReset(findNearestDropoffPoint(simulation));
		return this;
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
	protected CBehavior update(final CSimulation simulation, final boolean withinRange) {
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
					switch (this.abilityHarvest.getCarriedResourceType()) {
					case FOOD:
						throw new IllegalStateException("Unit used Harvest skill to carry FOOD resource!");
					case GOLD:
						player.setGold(player.getGold() + this.abilityHarvest.getCarriedResourceAmount());
						this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.GOLD);
						break;
					case LUMBER:
						player.setLumber(player.getLumber() + this.abilityHarvest.getCarriedResourceAmount());
						this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.LUMBER);
						break;
					}
					this.abilityHarvest.setCarriedResources(null, 0);
					final CUnit nearestMine = findNearestMine(this.unit, this.simulation);
					if (nearestMine != null) {
						return this.abilityHarvest.getBehaviorHarvest().reset(nearestMine);
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
		final boolean aliveCheck = this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
		if (!aliveCheck) {
			final CUnit nearestDropoff = findNearestDropoffPoint(simulation);
			if (nearestDropoff == null) {
				return false;
			}
			else {
				this.target = nearestDropoff;
				return true;
			}
		}
		return true;
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	private CUnit findNearestDropoffPoint(final CSimulation simulation) {
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

	private static CUnit findNearestMine(final CUnit worker, final CSimulation simulation) {
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

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game) {

	}

}
