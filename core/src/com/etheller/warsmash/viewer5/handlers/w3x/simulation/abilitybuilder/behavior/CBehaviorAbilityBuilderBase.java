package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.human.paladin.CBehaviorHolyLight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CBehaviorAbilityBuilderBase extends CAbstractRangedBehavior {
	private final AbilityBuilderConfiguration parser;
	private Map<String, Object> localStore;
	private AbilityBuilderAbility ability;
	private AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;
	
	private int castStartTick = 0;
	private boolean doneEffect = false;

	public CBehaviorAbilityBuilderBase(final CUnit unit, final AbilityBuilderConfiguration parser,
			final Map<String, Object> localStore, AbilityBuilderAbility ability) {
		super(unit);
		this.parser = parser;
		this.localStore = localStore;
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	public CBehaviorAbilityBuilderBase reset(final CWidget target) {
		innerReset(target, false);
		this.doneEffect = false;
		this.castStartTick = 0;
		for (ABAction action : parser.getOnResetCasting()) {
			action.runAction(null, null, localStore);
		}
		return this;
	}

	public CBehaviorAbilityBuilderBase reset(final AbilityPointTarget target) {
		innerReset(target, false);
		this.doneEffect = false;
		this.castStartTick = 0;
		for (ABAction action : parser.getOnResetCasting()) {
			action.runAction(null, null, localStore);
		}
		return this;
	}

	public CBehaviorAbilityBuilderBase reset() {
		innerReset(null, false);
		this.doneEffect = false;
		this.castStartTick = 0;
		for (ABAction action : parser.getOnResetCasting()) {
			action.runAction(null, null, localStore);
		}
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game, boolean withinFacingWindow) {
		this.unit.getUnitAnimationListener().playAnimation(false, null, SequenceUtils.SPELL, 1.0f, true);
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();
		}
		final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
		final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		if (!this.doneEffect  && (ticksSinceCast >= castPointTicks || ticksSinceCast >= backswingTicks)) {
			this.doneEffect = true;
			if (!this.unit.chargeMana(this.ability.getLevelData().get(ability.getLevel()).getManaCost())) {
				game.getCommandErrorListener().showNoManaError(this.unit.getPlayerIndex());
				return this.unit.pollNextOrderBehavior(game);
			}
			
			for (ABAction action : parser.getOnUpdateCasting()) {
				action.runAction(game, null, localStore);
			}
			this.ability.startCooldown(this.unit);
			this.unit.fireCooldownsChangedEvent();
		}
		if (ticksSinceCast >= backswingTicks) {
			return this.unit.pollNextOrderBehavior(game);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
		for (ABAction action : parser.getOnBeginCasting()) {
			action.runAction(game, null, localStore);
		}
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		for (ABAction action : parser.getOnEndCasting()) {
			action.runAction(game, null, localStore);
		}
	}

	@Override
	public int getHighlightOrderId() {
		return this.ability.getBaseOrderId();
	}

	@Override
	public boolean isWithinRange(CSimulation simulation) {
		List<CAbilityTypeAbilityBuilderLevelData> levelData = this.ability.getLevelData();
		final float castRange = levelData.get((this.ability.getLevel()) - 1).getCastRange();
		return this.unit.canReach(this.target, castRange);
	}

	@Override
	public void endMove(CSimulation game, boolean interrupted) {
	}

	@Override
	protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(CSimulation simulation) {
		List<CAbilityTypeAbilityBuilderLevelData> levelData = this.ability.getLevelData();
		EnumSet<CTargetType> targetsAllowed = levelData.get((this.ability.getLevel()) - 1).getTargetsAllowed();
		return this.target.visit(this.stillAliveVisitor.reset(simulation, this.unit, targetsAllowed));
	}

	@Override
	protected void resetBeforeMoving(CSimulation simulation) {
	}

}
