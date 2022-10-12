package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root;

import java.awt.image.BufferedImage;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorRoot  extends CAbstractRangedBehavior {
	private final CAbilityRoot abilityRoot;
	private int rootFinishTick;

	public CBehaviorRoot(CUnit unit, CAbilityRoot abilityRoot) {
		super(unit);
		this.abilityRoot = abilityRoot;
	}
	
	public CAbstractRangedBehavior reset(AbilityPointTarget pointTarget) {
		rootFinishTick = -1;
		return this.innerReset(pointTarget);
	}

	@Override
	public boolean isWithinRange(CSimulation simulation) {
//		return ((AbilityPointTarget)target).dst2(unit.getX(), unit.getY()) <= 0.1;
		return this.unit.canReach(this.target.getX(), this.target.getY(), 0);
	}

	@Override
	public void endMove(CSimulation game, boolean interrupted) {
	}

	@Override
	public void begin(CSimulation game) {
	}

	@Override
	public void end(CSimulation game, boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.root;
	}

	@Override
	protected CBehavior update(CSimulation simulation, boolean withinFacingWindow) {
		float duration = abilityRoot.getDuration();
		if(rootFinishTick == -1) {
			unit.setPoint(target.getX(), target.getY(), simulation.getWorldCollision(), simulation.getRegionManager());
			unit.setFacing(simulation.getGameplayConstants().getRootAngle());
			abilityRoot.setRooted(true, unit, simulation);
			
			rootFinishTick = simulation.getGameTurnTick() + (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
			unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.MORPH, SequenceUtils.EMPTY, duration, true);
		}
		else if(simulation.getGameTurnTick() >= rootFinishTick) {
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
			unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
			return unit.pollNextOrderBehavior(simulation);
		}
		return this;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(CSimulation simulation) {
		return true;
	}

	@Override
	protected void resetBeforeMoving(CSimulation simulation) {
	}

}
