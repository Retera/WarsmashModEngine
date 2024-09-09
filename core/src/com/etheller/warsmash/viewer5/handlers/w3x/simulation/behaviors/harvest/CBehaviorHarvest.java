package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CBehaviorHarvest extends CAbstractRangedBehavior
		implements AbilityTargetVisitor<CBehavior>, CBehaviorAttackListener {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;
	private int popoutFromMineTurnTick = 0;

	public CBehaviorHarvest(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit);
		this.abilityHarvest = abilityHarvest;
	}

	public CBehavior reset(final CSimulation game, final CWidget target) {
		this.abilityHarvest.setLastHarvestTarget(target);
		if (this.popoutFromMineTurnTick != 0) {
			// TODO this check is probably only for debug and should be removed after
			// extensive testing
			throw new IllegalStateException("A unit took action while within a gold mine.");
		}
		return innerReset(game, target, target instanceof CUnit);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.abilityHarvest.getTreeAttack().getRange());
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.harvest;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.simulation = simulation;
		return this.target.visit(this);
	}

	@Override
	public CBehavior accept(final AbilityPointTarget target) {
		return CBehaviorHarvest.this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	public CBehavior accept(final CUnit target) {
		if ((this.abilityHarvest.getCarriedResourceAmount() == 0)
				|| (this.abilityHarvest.getCarriedResourceType() != ResourceType.GOLD)) {
			for (final CAbility ability : target.getAbilities()) {
				if (ability instanceof CAbilityGoldMinable) {
					final CAbilityGoldMinable abilityGoldMine = (CAbilityGoldMinable) ability;
					final int activeMiners = abilityGoldMine.getActiveMinerCount();
					if (activeMiners < abilityGoldMine.getMiningCapacity()) {
						abilityGoldMine.addMiner(this);
						this.unit.setHidden(true);
						this.unit.setInvulnerable(true);
						this.unit.setPaused(true);
						this.unit.setAcceptingOrders(false);
						this.popoutFromMineTurnTick = this.simulation.getGameTurnTick()
								+ (int) (abilityGoldMine.getMiningDuration() / WarsmashConstants.SIMULATION_STEP_TIME);
					}
					else {
						// we are stuck waiting to mine, let's make sure we play stand animation
						this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY,
								1.0f, true);
					}
					return this;
				}
			}
			// weird invalid target and we have no resources, consider harvesting done
			if (this.abilityHarvest.getCarriedResourceAmount() == 0) {
				return this.unit.pollNextOrderBehavior(this.simulation);
			}
			else {
				return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
			}
		}
		else {
			// we have some GOLD and we're not in a mine (?) lets do a return resources
			// order
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
	}

	public void popoutFromMine(final int goldMined) {
		this.popoutFromMineTurnTick = 0;
		this.unit.setHidden(false);
		this.unit.setInvulnerable(false);
		this.unit.setPaused(false);
		this.unit.setAcceptingOrders(true);
		dropResources();
		this.abilityHarvest.setCarriedResources(ResourceType.GOLD, goldMined);
		if (this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.GOLD)) {
			this.unit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
		this.simulation.unitRepositioned(this.unit);
	}

	@Override
	public CBehavior accept(final CDestructable target) {
		if ((this.abilityHarvest.getCarriedResourceType() != ResourceType.LUMBER)
				|| (this.abilityHarvest.getCarriedResourceAmount() < this.abilityHarvest.getLumberCapacity())) {
			return this.abilityHarvest.getBehaviorTreeAttack().reset(this.simulation, getHighlightOrderId(),
					this.abilityHarvest.getTreeAttack(), target, false, this);
		}
		else {
			// we have some LUMBER and we can't carry any more, time to return resources
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
	}

	@Override
	public void onHit(final AbilityTarget target, final float damage) {
		if (this.abilityHarvest.getCarriedResourceType() != ResourceType.LUMBER) {
			dropResources();
		}
		this.abilityHarvest.setCarriedResources(ResourceType.LUMBER,
				Math.min(this.abilityHarvest.getCarriedResourceAmount() + this.abilityHarvest.getDamageToTree(),
						this.abilityHarvest.getLumberCapacity()));
		if (this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.LUMBER)) {
			this.unit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
		if (target instanceof CDestructable) {
			if (this.unit.getUnitType().getClassifications().contains(CUnitClassification.UNDEAD)) {
				((CDestructable) target).setBlighted(true);
			}
		}
	}

	@Override
	public void onLaunch() {

	}

	@Override
	public CBehavior onFirstUpdateAfterBackswing(final CBehaviorAttack currentAttackBehavior) {
		if (this.abilityHarvest.getCarriedResourceAmount() >= this.abilityHarvest.getLumberCapacity()) {
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
		return currentAttackBehavior;
	}

	@Override
	public CBehavior onFinish(final CSimulation game, final CUnit finishingUnit) {
		if (this.abilityHarvest.getCarriedResourceAmount() >= this.abilityHarvest.getLumberCapacity()) {
			return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
		}
		return updateOnInvalidTarget(game);
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
		if (this.target instanceof CDestructable) {
			// wood
			final CDestructable nearestTree = CBehaviorReturnResources.findNearestTree(this.unit, this.abilityHarvest,
					simulation, this.unit);
			if (nearestTree != null) {
				return reset(simulation, nearestTree);
			}
		}
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	public int getPopoutFromMineTurnTick() {
		return this.popoutFromMineTurnTick;
	}

	public int getGoldCapacity() {
		return this.abilityHarvest.getGoldCapacity();
	}

	private void dropResources() {
		final ResourceType carriedResourceType = this.abilityHarvest.getCarriedResourceType();
		if ((carriedResourceType != null) && (this.abilityHarvest.getCarriedResourceAmount() > 0)) {
			SecondaryTag removedTag = null;
			switch (carriedResourceType) {
			case GOLD:
				removedTag = SecondaryTag.GOLD;
				break;
			case LUMBER:
				removedTag = SecondaryTag.LUMBER;
				break;
			default:
				break;
			}
			if (removedTag == null) {
				throw new IllegalStateException(
						"Unit used Harvest skill to carry " + carriedResourceType + " resource!");
			}
			if (this.unit.getUnitAnimationListener().removeSecondaryTag(removedTag)) {
				this.unit.getUnitAnimationListener().forceResetCurrentAnimation();
			}
		}
		this.abilityHarvest.setCarriedResources(null, 0);
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
