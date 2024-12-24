package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.eattree;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityEatTree extends CAbilityTargetSpellBase {
	private float ripDelay;
	private float eatDelay;
	private float hitPointsGained;
	private War3ID buffId;

	private int ripEndTick = 0;
	private int eatEndTick = 0;
	private boolean ripComplete = false;

	public CAbilityEatTree(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.eattree;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		ripDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		eatDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		hitPointsGained = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		setCastingSecondaryTags(SequenceUtils.SPELL_EATTREE);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		final float castRange = getCastRange();
		final float closeEnoughRange = game.getGameplayConstants().getCloseEnoughRange();
		if (castRange < closeEnoughRange) {
			// help with large collision size of buildings... someday maybe this shouldnt be
			// needed
			setCastRange(castRange + closeEnoughRange);
		}
		super.onAdd(game, unit);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == OrderIds.smart) {
			super.checkCanTarget(game, unit, getBaseOrderId(), target, receiver);
		}
		else {
			super.checkCanTarget(game, unit, orderId, target, receiver);
		}
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target.visit(AbilityTargetVisitor.DESTRUCTABLE) == null) {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_TREE);
		}
		else {
			super.innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick();
		ripEndTick = gameTurnTick + (int) (ripDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		eatEndTick = ripEndTick + (int) ((eatDelay) / WarsmashConstants.SIMULATION_STEP_TIME);
		ripComplete = false;
		return true;
	}

	@Override
	public boolean doChannelTick(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick();
		if (gameTurnTick >= ripEndTick) {
			if (!ripComplete) {
				final CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
				if (targetDest != null) {
					unit.add(simulation, new CBuffEatTree(simulation.getHandleIdAllocator().createId(), buffId, 1.0f,
							getDuration(), hitPointsGained));
					targetDest.setLife(simulation, 0);
					simulation.createTemporarySpellEffectOnUnit(unit, getAlias(), CEffectType.SPECIAL);
				}
				ripComplete = true;
			}
			if (gameTurnTick >= eatEndTick) {
				return false;
			}
		}
		return true;
	}
}
