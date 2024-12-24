package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CBehaviorAcolyteHarvest extends CAbstractRangedBehavior {
	private final CAbilityAcolyteHarvest abilityAcolyteHarvest;
	private boolean harvesting = false;
	private float harvestStandX, harvestStandY;

	public CBehaviorAcolyteHarvest(final CUnit unit, final CAbilityAcolyteHarvest abilityWispHarvest) {
		super(unit);
		this.abilityAcolyteHarvest = abilityWispHarvest;
	}

	public CBehavior reset(CSimulation game, final CWidget target) {
		return innerReset(game, target, false);
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		if (!this.harvesting) {
			final HarvestStartResult result = onStartHarvesting(simulation);
			if (result == HarvestStartResult.DENIED) {
				simulation.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(), CommandStringErrorKeys.THAT_GOLD_MINE_CANT_SUPPORT_ANY_MORE_ACOLYTES);
				return this.unit.pollNextOrderBehavior(simulation);
			}
			else if (result == HarvestStartResult.ACCEPTED) {
				this.harvesting = true;
			}
			else {
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
						true);
			}
		}
		if (this.harvesting) {
			if ((this.harvestStandX != this.unit.getX()) || (this.harvestStandY != this.unit.getY())) {
				this.unit.setX(this.harvestStandX, simulation.getWorldCollision(), simulation.getRegionManager());
				this.unit.setY(this.harvestStandY, simulation.getWorldCollision(), simulation.getRegionManager());
				simulation.unitRepositioned(this.unit); // dont interpolate, instant jump
			}
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.WORK, 1.0f, true);
		}
		return this;
	}

	private HarvestStartResult onStartHarvesting(final CSimulation simulation) {
		// TODO maybe use visitor instead of cast
		final CUnit targetUnit = (CUnit) this.target;
		for (final CAbility ability : targetUnit.getAbilities()) {
			if ((ability instanceof CAbilityBlightedGoldMine) && !ability.isDisabled()) {
				final CAbilityBlightedGoldMine abilityBlightedGoldMine = (CAbilityBlightedGoldMine) ability;
				final int newIndex = abilityBlightedGoldMine.tryAddMiner(this.unit, this);
				if (newIndex == CAbilityBlightedGoldMine.NO_MINER) {
					return HarvestStartResult.DENIED;
				}

				final Vector2 minerLoc = abilityBlightedGoldMine.getMinerLoc(newIndex);
				this.harvestStandX = minerLoc.x;
				this.harvestStandY = minerLoc.y;
				simulation.unitSoundEffectEvent(this.unit, this.abilityAcolyteHarvest.getAlias());
				return HarvestStartResult.ACCEPTED;
			}
		}
		return HarvestStartResult.WAITING;
	}

	private void onStopHarvesting(final CSimulation simulation) {
		final CUnit targetUnit = (CUnit) this.target;
		for (final CAbility ability : targetUnit.getAbilities()) {
			if (ability instanceof CAbilityBlightedGoldMine) {
				((CAbilityBlightedGoldMine) ability).removeMiner(this);
			}

		}
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		if (this.harvesting) {
			onStopHarvesting(simulation);
			this.harvesting = false;
		}
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		if (this.harvesting) {
			onStopHarvesting(simulation);
			this.harvesting = false;
		}
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, this.abilityAcolyteHarvest.getCastRange());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		if (this.harvesting) {
			onStopHarvesting(game);
			this.harvesting = false;
		}
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.acolyteharvest;
	}

	private static enum HarvestStartResult {
		WAITING,
		DENIED,
		ACCEPTED
	};

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}
}
