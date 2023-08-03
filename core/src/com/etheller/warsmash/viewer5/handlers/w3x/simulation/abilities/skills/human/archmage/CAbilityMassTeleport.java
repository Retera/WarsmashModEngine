package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;

import java.util.ArrayList;
import java.util.List;

public class CAbilityMassTeleport extends CAbilityTargetSpellBase {

	private int numberOfUnitsTeleported;
	private boolean useTeleportClustering;
	private float castingDelay;
	private float areaOfEffect;

	private int channelEndTick;
	private SimulationRenderComponentModel sourceAreaEffectRenderComponent;
	private SimulationRenderComponentModel targetAreaEffectRenderComponent;

	public CAbilityMassTeleport(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.massteleport;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		numberOfUnitsTeleported =
				worldEditorAbility.getFieldAsInteger(AbilityFields.MassTeleport.NUMBER_OF_UNITS_TELEPORTED, level);
		useTeleportClustering =
				worldEditorAbility.getFieldAsBoolean(AbilityFields.MassTeleport.USE_TELEPORT_CLUSTERING, level);
		castingDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.MassTeleport.CASTING_DELAY, level);
		areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		this.channelEndTick =
				simulation.getGameTurnTick() + (int) StrictMath.ceil(castingDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		sourceAreaEffectRenderComponent = simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0, getAlias(),
				CEffectType.AREA_EFFECT, 0);
		targetAreaEffectRenderComponent = simulation.spawnSpellEffectOnPoint(target.getX(), target.getY(), 0, getAlias(),
				CEffectType.AREA_EFFECT, 0);
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if(targetUnit != null) {
			targetUnit.setPaused(true);
		}
		return true;
	}

	@Override
	public boolean doChannelTick(CSimulation simulation, CUnit caster, AbilityTarget target) {
		int gameTurnTick = simulation.getGameTurnTick();
		if (gameTurnTick >= channelEndTick) {
			List<CUnit> teleportingUnits = new ArrayList<>();
			float casterX = caster.getX();
			float casterY = caster.getY();
			float targetX = target.getX();
			float targetY = target.getY();
			simulation.getWorldCollision().enumUnitsInRange(casterX, casterY, areaOfEffect, (enumUnit) -> {
				if (enumUnit != caster && enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
					teleportingUnits.add(enumUnit);
				}
				return teleportingUnits.size() + 1 >= numberOfUnitsTeleported;
			});
			for(CUnit teleportingUnit: teleportingUnits) {
				simulation.spawnSpellEffectOnPoint(teleportingUnit.getX(), teleportingUnit.getY(), 0, getAlias(),
						CEffectType.SPECIAL, 0).remove();
			}
			simulation.spawnSpellEffectOnPoint(casterX, casterY, 0, getAlias(),
					CEffectType.SPECIAL, 0).remove();
			caster.setPointAndCheckUnstuck(targetX, targetY, simulation);
			if(useTeleportClustering) {
				for(CUnit teleportingUnit: teleportingUnits) {
					teleportingUnit.setPointAndCheckUnstuck(targetX, targetY, simulation);
				}
			} else {
				for(CUnit teleportingUnit: teleportingUnits) {
					float offsetX = teleportingUnit.getX() - casterX;
					float offsetY = teleportingUnit.getY() - casterY;
					teleportingUnit.setPointAndCheckUnstuck(targetX + offsetX, targetY + offsetY, simulation);
				}
			}
			for(CUnit teleportingUnit: teleportingUnits) {
				simulation.spawnSpellEffectOnPoint(teleportingUnit.getX(), teleportingUnit.getY(), 0, getAlias(),
						CEffectType.SPECIAL, 0).remove();
			}
			simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0, getAlias(),
					CEffectType.SPECIAL, 0).remove();
			return false;
		}
		return true;
	}

	@Override
	public void doChannelEnd(CSimulation game, CUnit unit, AbilityTarget target, boolean interrupted) {
		sourceAreaEffectRenderComponent.remove();
		sourceAreaEffectRenderComponent = null;
		targetAreaEffectRenderComponent.remove();
		targetAreaEffectRenderComponent = null;
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if(targetUnit != null) {
			targetUnit.setPaused(false);
		}
	}
}
