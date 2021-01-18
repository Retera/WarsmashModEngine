package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CBehaviorHarvest extends CAbstractRangedBehavior implements AbilityTargetVisitor<CBehavior> {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;
	private int popoutFromMineTurnTick = 0;
	private CAbilityGoldMine abilityGoldMine;

	public CBehaviorHarvest(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit, true);
		this.abilityHarvest = abilityHarvest;
	}

	public CBehaviorHarvest reset(final CWidget target) {
		innerReset(target);
		this.abilityGoldMine = null;
		if (this.popoutFromMineTurnTick != 0) {
			// TODO this check is probably only for debug and should be removed after
			// extensive testing
			throw new IllegalStateException("A unit took action while within a gold mine.");
		}
		return this;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.unit.getUnitType().getCollisionSize());
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.harvest;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinRange) {
		this.simulation = simulation;
		return this.target.visit(this);
	}

	@Override
	public CBehavior accept(final AbilityPointTarget target) {
		return CBehaviorHarvest.this.unit.pollNextOrderBehavior(this.simulation);
	}

	@Override
	public CBehavior accept(final CUnit target) {
		if (this.popoutFromMineTurnTick == 0) {
			if ((this.abilityHarvest.getCarriedResourceAmount() == 0)
					|| (this.abilityHarvest.getCarriedResourceType() != ResourceType.GOLD)) {
				for (final CAbility ability : target.getAbilities()) {
					if (ability instanceof CAbilityGoldMine) {
						final CAbilityGoldMine abilityGoldMine = (CAbilityGoldMine) ability;
						final int activeMiners = abilityGoldMine.getActiveMiners();
						if (activeMiners < abilityGoldMine.getMiningCapacity()) {
							abilityGoldMine.setActiveMiners(activeMiners + 1);
							if (activeMiners == 0) {
								target.getUnitAnimationListener().addSecondaryTag(SecondaryTag.WORK);
							}
							this.unit.setHidden(true);
							this.unit.setInvulnerable(true);
							this.popoutFromMineTurnTick = this.simulation.getGameTurnTick()
									+ (int) (abilityGoldMine.getMiningDuration()
											/ WarsmashConstants.SIMULATION_STEP_TIME);
							this.abilityGoldMine = abilityGoldMine;
						}
						else {
							// we are stuck waiting to mine, let's make sure we play stand animation
							this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
									SequenceUtils.EMPTY, 1.0f, true);
						}
						return this;
					}
				}
				// weird invalid target and we have no resources, consider harvesting done
				return this.unit.pollNextOrderBehavior(this.simulation);
			}
			else {
				// we have some GOLD and we're not in a mine (?) lets do a return resources
				// order
				return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
			}
		}
		else {
			if (this.simulation.getGameTurnTick() >= this.popoutFromMineTurnTick) {
				this.popoutFromMineTurnTick = 0;
				this.unit.setHidden(false);
				this.unit.setInvulnerable(false);
				final int activeMiners = this.abilityGoldMine.getActiveMiners() - 1;
				this.abilityGoldMine.setActiveMiners(activeMiners);
				if (activeMiners == 0) {
					target.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.WORK);
				}
				int mineGoldRemaining = this.abilityGoldMine.getGold();
				final int goldMined = Math.min(mineGoldRemaining, this.abilityHarvest.getGoldCapacity());
				this.abilityHarvest.setCarriedResources(ResourceType.GOLD, goldMined);
				mineGoldRemaining -= goldMined;
				this.abilityGoldMine.setGold(mineGoldRemaining);
				if (mineGoldRemaining <= 0) {
					target.setLife(this.simulation, 0);
				}
				this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.GOLD);
				this.simulation.unitRepositioned(this.unit);
				// we just finished getting gold, lets do a return resources order
				return this.abilityHarvest.getBehaviorReturnResources().reset(this.simulation);
			}
			else {
				// continue working inside mine
				return this;
			}
		}
	}

	@Override
	public CBehavior accept(final CDestructable target) {
		// TODO cut trees!
		if (String.valueOf(target).length() > 5) {
			return this.unit.pollNextOrderBehavior(this.simulation);
		}
		else {
			return null;
		}
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
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

}
