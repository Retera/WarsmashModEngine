package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root;

import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.AbilityDisableWhileUnderConstructionVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CAbilityEntangleGoldMine extends CAbilityTargetSpellBase {
	private War3ID resultingTypeId;

	private RemovableTriggerEvent mineDeathEvent;
	private final Trigger mineDeathTrigger = new Trigger();
	private CUnit entangledMine;
	private SimulationRenderComponent unitRootsRenderComponent;
	private boolean instant;

	public CAbilityEntangleGoldMine(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void onRemoveDisabled(final CSimulation game, final CUnit unit) {
		if (this.entangledMine != null) {
			unentangle(game);
		}
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.entangle;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		final String resultingType = worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0);
		this.resultingTypeId = resultingType.length() == 4 ? War3ID.fromString(resultingType) : War3ID.NONE;

		setCastingPrimaryTag(PrimaryTag.STAND);
		setCastingSecondaryTags(SequenceUtils.EMPTY);

		final EnumSet<CTargetType> targetsAllowed = getTargetsAllowed();
		// this ability ignores vulnerabilities, and will target invulnerable mines
		targetsAllowed.add(CTargetType.INVULNERABLE);
		targetsAllowed.add(CTargetType.VULNERABLE);
		setTargetsAllowed(targetsAllowed);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == OrderIds.entangleinstant) {
			orderId = getBaseOrderId();
		}
		super.checkCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		final CUnit unitTarget = target.visit(AbilityTargetVisitor.UNIT);
		if (unitTarget != null) {
			if (unitTarget.getGoldMineData() != null) {
				super.innerCheckCanTarget(game, unit, orderId, target, receiver);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_GOLD_MINE);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (this.entangledMine != null) {
			receiver.disabled();
		}
		else {
			super.innerCheckCanUse(game, unit, orderId, receiver);
		}
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		instant = orderId == OrderIds.entangleinstant;
		return super.begin(game, caster, orderId, target);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final CUnit unitTarget = target.visit(AbilityTargetVisitor.UNIT);
		if (unitTarget != null) {
			final CAbilityGoldMinable goldMineData = unitTarget.getGoldMineData();
			if (goldMineData != null && goldMineData.isBaseMine()) {
				unitTarget.setHidden(true);
				unitTarget.setPaused(true);
				// == stuff copied from build behavior ==
				this.entangledMine = simulation.createUnit(this.resultingTypeId, unit.getPlayerIndex(),
						unitTarget.getX(), unitTarget.getY(), simulation.getGameplayConstants().getBuildingAngle());

				if (!instant) {
					this.entangledMine.setConstructing(true);
					this.entangledMine.setLife(simulation,
							this.entangledMine.getMaximumLife() * WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE);
					this.entangledMine.add(simulation,
							new CAbilityBuildInProgress(simulation.getHandleIdAllocator().createId()));
					for (final CAbility ability : this.entangledMine.getAbilities()) {
						ability.visit(AbilityDisableWhileUnderConstructionVisitor.INSTANCE);
					}
					unit.checkDisabledAbilities(simulation, true);
				}
				this.entangledMine.setFoodUsed(this.entangledMine.getUnitType().getFoodUsed());
				simulation.getPlayer(unit.getPlayerIndex()).addTechtreeInProgress(this.resultingTypeId);
				simulation.unitConstructedEvent(unit, this.entangledMine);
				// == end stuff copied from build behavior (this was noted in case it refactors
				// to a common subroutine later) ==

				this.entangledMine.getOverlayedGoldMineData().setParentMine(unitTarget, goldMineData);
				this.unitRootsRenderComponent = simulation.createPersistentSpellEffectOnUnit(unit, getAlias(),
						CEffectType.CASTER, 0);

				setIconShowing(false);
				setPermanent(true);
				// TODO maybe don't literally use a Trigger type (?) or else use it for
				// everything to be consistent:
				if (this.mineDeathEvent != null) {
					this.mineDeathEvent.remove();
				}
				this.mineDeathTrigger.reset();
				this.mineDeathEvent = this.entangledMine.addDeathEvent(simulation.getGlobalScope(),
						this.mineDeathTrigger);
				this.mineDeathTrigger.addAction(new JassFunction() {
					@Override
					public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
							final TriggerExecutionScope triggerScope) {
						unentangle(simulation);
						return null;
					}
				});
			}

		}
		return false;
	}

	public void unentangle(final CSimulation game) {
		if (this.entangledMine != null) {
			this.entangledMine.kill(game);
			this.entangledMine = null;
		}
		if (this.unitRootsRenderComponent != null) {
			this.unitRootsRenderComponent.remove();
		}
		setIconShowing(true);
		setPermanent(false);
		if (this.mineDeathEvent != null) {
			this.mineDeathEvent.remove();
			this.mineDeathEvent = null;
		}
	}

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
		super.onSetUnitType(game, cUnit);
		if (this.entangledMine != null) {
			// NOTE: this is getting used here as a quick solution to a problem that is
			// better solved later by making setters (setIconShowing, etc, whatever)
			// use some type of increasing/decreasing count
			setIconShowing(false);
		}
	}

}
