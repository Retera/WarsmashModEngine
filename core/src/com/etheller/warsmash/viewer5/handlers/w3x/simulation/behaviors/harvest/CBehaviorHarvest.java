package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.util.WarsmashConstants;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CBehaviorHarvest extends CAbstractRangedBehavior implements AbilityTargetVisitor<CBehavior> {
	private final CAbilityHarvest abilityHarvest;
	private CSimulation simulation;
	private int popoutFromMineTurnTick = 0;
	private CAbilityGoldMine abilityGoldMine;

	public CBehaviorHarvest(final CUnit unit, final CAbilityHarvest abilityHarvest) {
		super(unit);
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
		// TODO this is probably not what the CloseEnoughRange constant is for
		return this.unit.canReach(this.target, simulation.getGameplayConstants().getCloseEnoughRange());
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
							this.unit.setHidden(true);
							this.unit.setInvulnerable(true);
							this.popoutFromMineTurnTick = this.simulation.getGameTurnTick()
									+ (int) (abilityGoldMine.getMiningDuration()
											/ WarsmashConstants.SIMULATION_STEP_TIME);
							this.abilityGoldMine = abilityGoldMine;
							break;
						}
					}
				}
			}
			else {
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
							return this.unit.pollNextOrderBehavior(this.simulation);
						}
					}
				}
			}
		}
		else {
			if (this.simulation.getGameTurnTick() >= this.popoutFromMineTurnTick) {
				this.popoutFromMineTurnTick = 0;
				this.unit.setHidden(false);
				this.unit.setInvulnerable(false);
				this.abilityGoldMine.setActiveMiners(this.abilityGoldMine.getActiveMiners() - 1);
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
				return this.unit.pollNextOrderBehavior(this.simulation);
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
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

}
