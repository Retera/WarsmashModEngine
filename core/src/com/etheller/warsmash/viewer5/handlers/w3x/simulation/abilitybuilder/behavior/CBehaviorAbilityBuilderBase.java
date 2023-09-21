package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CBehaviorAbilityBuilderBase extends CAbstractRangedBehavior {
	private final AbilityBuilderConfiguration parser;
	private Map<String, Object> localStore;
	private AbilityBuilderAbility ability;
	private ABAbilityTargetStillTargetableVisitor preCastTargetableVisitor;
	
	private int castStartTick = 0;
	private boolean doneEffect = false;
	private boolean channeling = false;
	
	private int castId = 0;

	public CBehaviorAbilityBuilderBase(final CUnit unit, final AbilityBuilderConfiguration parser,
			final Map<String, Object> localStore, AbilityBuilderAbility ability) {
		super(unit);
		this.parser = parser;
		this.localStore = localStore;
		this.ability = ability;
		this.preCastTargetableVisitor = new ABAbilityTargetStillTargetableVisitor();
		
	}

	public CBehaviorAbilityBuilderBase reset(final CWidget target) {
		innerReset(target, false);
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		return this;
	}

	public CBehaviorAbilityBuilderBase reset(final AbilityPointTarget target) {
		innerReset(target, false);
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		return this;
	}

	public CBehaviorAbilityBuilderBase reset() {
		innerReset(null, false);
		this.doneEffect = false;
		this.castStartTick = 0;
		this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game, boolean withinFacingWindow) {
		this.unit.getUnitAnimationListener().playAnimation(false, this.ability.getCastingPrimaryTag(),
				this.ability.getCastingSecondaryTags(), 1.0f, true);
		if (this.castStartTick == 0) {
			this.castStartTick = game.getGameTurnTick();
			
			if (!this.target.visit(this.preCastTargetableVisitor.reset(game, this.unit, ability, false))) {
				return this.unit.pollNextOrderBehavior(game);
			}
			
			if (!this.unit.chargeMana(this.ability.getUIManaCost())) {
				game.getCommandErrorListener().showInterfaceError(this.unit.getPlayerIndex(),
						CommandStringErrorKeys.NOT_ENOUGH_MANA);
				return this.unit.pollNextOrderBehavior(game);
			}
			this.ability.startCooldown(game, this.unit);
			
			if (parser.getOnBeginCasting() != null) {
				for (ABAction action : parser.getOnBeginCasting()) {
					action.runAction(game, this.unit, localStore, castId);
				}
			}
		}
		final int ticksSinceCast = game.getGameTurnTick() - this.castStartTick;
		final int castPointTicks = (int) (this.unit.getUnitType().getCastPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		final int backswingTicks = (int) (this.unit.getUnitType().getCastBackswingPoint()
				/ WarsmashConstants.SIMULATION_STEP_TIME);
		if ((ticksSinceCast >= castPointTicks) || (ticksSinceCast >= backswingTicks)) {
			boolean wasEffectDone = this.doneEffect;
			boolean wasChanneling = this.channeling;
			if (!wasEffectDone) {
				this.doneEffect = true;
				if (parser.getOnEndCasting() != null) {
					for (ABAction action : parser.getOnEndCasting()) {
						action.runAction(game, this.unit, localStore, castId);
					}
				}
				this.channeling = (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
				
				if (this.channeling) {
					game.unitLoopSoundEffectEvent(this.unit, this.ability.getAlias());
				}
				else {
					game.unitSoundEffectEvent(this.unit, this.ability.getAlias());
				}
			}
			this.channeling = this.channeling && this.doChannelTick(game, this.unit, this.target);
			if (wasEffectDone && wasChanneling && !this.channeling) {
				endChannel(game, false);
			}
		}
		if ((ticksSinceCast >= backswingTicks) && !this.channeling) {
			System.out.println("Removing targets");
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + castId);
			return this.unit.pollNextOrderBehavior(game);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
	}

	public boolean doChannelTick(CSimulation game, CUnit caster, AbilityTarget target) {
		if (parser.getOnChannelTick() != null) {
			for (ABAction action : parser.getOnChannelTick()) {
				action.runAction(game, this.unit, localStore, castId);
			}
		}
		return (boolean) localStore.get(ABLocalStoreKeys.CHANNELING);
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
		checkEndChannel(game, interrupted);
	}

	private void checkEndChannel(final CSimulation game, final boolean interrupted) {
		if (this.channeling) {
			this.channeling = false;
			this.localStore.put(ABLocalStoreKeys.CHANNELING, false);
			endChannel(game, interrupted);
		}
	}

	private void endChannel(CSimulation game, boolean interrupted) {
		this.localStore.put(ABLocalStoreKeys.INTERRUPTED, interrupted);
		game.unitStopSoundEffectEvent(this.unit, this.ability.getAlias());
		if (parser.getOnEndChannel() != null) {
			for (ABAction action : parser.getOnEndChannel()) {
				action.runAction(game, this.unit, localStore, castId);
			}
		}
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
		this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + castId);
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
		if (interrupted) {
			checkEndChannel(game, interrupted);
		}
	}

	@Override
	protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(CSimulation simulation) {
		return this.target.visit(this.preCastTargetableVisitor.reset(simulation, this.unit, this.ability, this.channeling));
	}

	@Override
	protected void resetBeforeMoving(CSimulation simulation) {
		this.castStartTick = 0;
	}
	
	public void setCastId(int castId) {
		this.castId = castId;
	}

}
