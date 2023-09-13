package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.darkranger;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityCharm extends CAbilityTargetSpellBase {
	private int maximumCreepLevel;

	public CAbilityCharm(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.maximumCreepLevel = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.charm;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			if (targetUnit.getUnitType().getLevel() <= maximumCreepLevel) {
				super.innerCheckCanTarget(game, unit, orderId, target, receiver);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.THAT_CREATURE_IS_TOO_POWERFUL);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
		final List<War3ID> targetUpgradesUsed = targetUnit.getUnitType().getUpgradesUsed();
		final CPlayer targetPlayer = simulation.getPlayer(targetUnit.getPlayerIndex());
		final CPlayer castingUnitPlayer = simulation.getPlayer(unit.getPlayerIndex());
		for (final War3ID targetUpgradeUsed : targetUpgradesUsed) {
			final CUpgradeType upgradeType = simulation.getUpgradeData().getType(targetUpgradeUsed);
			if (upgradeType.isTransferWithUnitOwnership()) {
				final int targetPlayerTechUnlocked = targetPlayer.getTechtreeUnlocked(targetUpgradeUsed);
				final int castingUnitPlayerTechUnlocked = castingUnitPlayer.getTechtreeUnlocked(targetUpgradeUsed);
				if (targetPlayerTechUnlocked > castingUnitPlayerTechUnlocked) {
					castingUnitPlayer.setTechResearched(simulation, targetUpgradeUsed, targetPlayerTechUnlocked);
				}
			}
		}
		simulation.getUnitData().unapplyPlayerUpgradesToUnit(simulation, targetUnit.getPlayerIndex(),
				targetUnit.getUnitType(), targetUnit);
		final int oldFoodUsed = targetUnit.getFoodUsed();
		targetPlayer.setUnitFoodUsed(targetUnit, 0);
		targetUnit.setPlayerIndex(simulation, unit.getPlayerIndex(), true);
		simulation.getUnitData().applyPlayerUpgradesToUnit(simulation, targetUnit.getPlayerIndex(),
				targetUnit.getUnitType(), targetUnit);
		castingUnitPlayer.setUnitFoodUsed(targetUnit, oldFoodUsed);
		targetUnit.order(simulation, OrderIds.stop, null);
		return false;
	}

	public int getMaximumCreepLevel() {
		return maximumCreepLevel;
	}

	public void setMaximumCreepLevel(final int maximumCreepLevel) {
		this.maximumCreepLevel = maximumCreepLevel;
	}
}
