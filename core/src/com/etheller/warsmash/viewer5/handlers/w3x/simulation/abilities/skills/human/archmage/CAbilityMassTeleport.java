package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;

public class CAbilityMassTeleport extends CAbilityTargetSpellBase {

	private int numberOfUnitsTeleported;
	private boolean useTeleportClustering;
	private float castingDelay;
	private float areaOfEffect;

	private int channelEndTick;
	private SimulationRenderComponentModel sourceAreaEffectRenderComponent;
	private SimulationRenderComponentModel targetAreaEffectRenderComponent;

	public CAbilityMassTeleport(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.massteleport;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		numberOfUnitsTeleported = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
		useTeleportClustering = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_C + level, 0);
		castingDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		this.channelEndTick = simulation.getGameTurnTick()
				+ (int) StrictMath.ceil(castingDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		sourceAreaEffectRenderComponent = simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0,
				getAlias(), CEffectType.AREA_EFFECT, 0);
		targetAreaEffectRenderComponent = simulation.spawnSpellEffectOnPoint(target.getX(), target.getY(), 0,
				getAlias(), CEffectType.AREA_EFFECT, 0);
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			targetUnit.setPaused(true);
		}
		return true;
	}

	@Override
	public boolean doChannelTick(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick();
		if (gameTurnTick >= channelEndTick) {
			final List<CUnit> teleportingUnits = new ArrayList<>();
			final float casterX = caster.getX();
			final float casterY = caster.getY();
			final float targetX = target.getX();
			final float targetY = target.getY();
			simulation.getWorldCollision().enumUnitsInRange(casterX, casterY, areaOfEffect, (enumUnit) -> {
				if ((enumUnit != caster) && enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
					teleportingUnits.add(enumUnit);
				}
				return (teleportingUnits.size() + 1) >= numberOfUnitsTeleported;
			});
			for (final CUnit teleportingUnit : teleportingUnits) {
				simulation.spawnSpellEffectOnPoint(teleportingUnit.getX(), teleportingUnit.getY(), 0, getAlias(),
						CEffectType.SPECIAL, 0).remove();
			}
			simulation.spawnSpellEffectOnPoint(casterX, casterY, 0, getAlias(), CEffectType.SPECIAL, 0).remove();
			caster.setPointAndCheckUnstuck(targetX, targetY, simulation);
			if (useTeleportClustering) {
				for (final CUnit teleportingUnit : teleportingUnits) {
					teleportingUnit.setPointAndCheckUnstuck(targetX, targetY, simulation);
				}
			}
			else {
				for (final CUnit teleportingUnit : teleportingUnits) {
					final float offsetX = teleportingUnit.getX() - casterX;
					final float offsetY = teleportingUnit.getY() - casterY;
					teleportingUnit.setPointAndCheckUnstuck(targetX + offsetX, targetY + offsetY, simulation);
				}
			}
			for (final CUnit teleportingUnit : teleportingUnits) {
				simulation.spawnSpellEffectOnPoint(teleportingUnit.getX(), teleportingUnit.getY(), 0, getAlias(),
						CEffectType.SPECIAL, 0).remove();
			}
			simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0, getAlias(), CEffectType.SPECIAL, 0)
					.remove();
			return false;
		}
		return true;
	}

	@Override
	public void doChannelEnd(final CSimulation game, final CUnit unit, final AbilityTarget target,
			final boolean interrupted) {
		sourceAreaEffectRenderComponent.remove();
		sourceAreaEffectRenderComponent = null;
		targetAreaEffectRenderComponent.remove();
		targetAreaEffectRenderComponent = null;
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			targetUnit.setPaused(false);
		}
	}
}
