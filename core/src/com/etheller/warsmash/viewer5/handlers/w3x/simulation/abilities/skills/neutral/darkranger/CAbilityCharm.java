package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.darkranger;

import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class CAbilityCharm extends CAbilityTargetSpellBase {
	private int maximumCreepLevel;

	public CAbilityCharm(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(MutableGameObject worldEditorAbility, int level) {
		this.maximumCreepLevel = worldEditorAbility.getFieldAsInteger(AbilityFields.CHARM_MAX_CREEP_LEVEL, level);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.charm;
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		simulation.createSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
		List<War3ID> targetUpgradesUsed = targetUnit.getUnitType().getUpgradesUsed();
		CPlayer targetPlayer = simulation.getPlayer(targetUnit.getPlayerIndex());
		CPlayer castingUnitPlayer = simulation.getPlayer(unit.getPlayerIndex());
		for (War3ID targetUpgradeUsed : targetUpgradesUsed) {
			CUpgradeType upgradeType = simulation.getUpgradeData().getType(targetUpgradeUsed);
			if (upgradeType.isTransferWithUnitOwnership()) {
				int targetPlayerTechUnlocked = targetPlayer.getTechtreeUnlocked(targetUpgradeUsed);
				int castingUnitPlayerTechUnlocked = castingUnitPlayer.getTechtreeUnlocked(targetUpgradeUsed);
				if (targetPlayerTechUnlocked > castingUnitPlayerTechUnlocked) {
					castingUnitPlayer.setTechResearched(simulation, targetUpgradeUsed, targetPlayerTechUnlocked);
				}
			}
		}
		simulation.getUnitData().unapplyPlayerUpgradesToUnit(simulation, targetUnit.getPlayerIndex(),
				targetUnit.getUnitType(), targetUnit);
		targetUnit.setPlayerIndex(simulation, unit.getPlayerIndex(), true);
		simulation.getUnitData().applyPlayerUpgradesToUnit(simulation, targetUnit.getPlayerIndex(),
				targetUnit.getUnitType(), targetUnit);
		targetUnit.order(simulation, OrderIds.stop, null);
		return false;
	}

	public int getMaximumCreepLevel() {
		return maximumCreepLevel;
	}

	public void setMaximumCreepLevel(int maximumCreepLevel) {
		this.maximumCreepLevel = maximumCreepLevel;
	}
}
