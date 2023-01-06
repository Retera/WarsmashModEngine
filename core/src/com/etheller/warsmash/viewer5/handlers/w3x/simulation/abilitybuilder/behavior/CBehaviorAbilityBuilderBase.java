package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorAbilityBuilderBase extends CAbstractRangedBehavior {
	private final AbilityBuilderParser parser;
	private Map<String, Object> localStore;

	public CBehaviorAbilityBuilderBase(final CUnit unit, final AbilityBuilderParser parser, final Map<String, Object> localStore) {
		super(unit);
		this.parser = parser;
		this.localStore = localStore;
	}

	public CBehaviorAbilityBuilderBase reset() {
		for(ABAction action : parser.getOnResetCasting()) {
			action.runAction(null, null, localStore);
		}
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game, boolean withinFacingWindow) {
		for(ABAction action : parser.getOnUpdateCasting()) {
			action.runAction(game, null, localStore);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
		for(ABAction action : parser.getOnBeginCasting()) {
			action.runAction(game, null, localStore);
		}
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		for(ABAction action : parser.getOnEndCasting()) {
			action.runAction(game, null, localStore);
		}
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.channel;
	}

	@Override
	public boolean isWithinRange(CSimulation simulation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void endMove(CSimulation game, boolean interrupted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean checkTargetStillValid(CSimulation simulation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void resetBeforeMoving(CSimulation simulation) {
		// TODO Auto-generated method stub
		
	}

}
