package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.bloodmage.phoenix;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class CAbilitySummonPhoenix extends CAbilityNoTargetSpellBase {
	private War3ID summonUnitId;
	private int summonUnitCount;
	private War3ID buffId;
	private float areaOfEffect;

	// TODO maybe "lastSummonHandleIds" instead, for ease of use with saving game,
	// but then we have to track when they die or else risk re-used handle ID
	// messing us up
	private final List<CUnit> lastSummonUnits = new ArrayList<>();

	public CAbilitySummonPhoenix(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.summonUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
		this.summonUnitCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.summonphoenix;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final float facing = unit.getFacing();
		final float facingRad = (float) StrictMath.toRadians(facing);
		final float x = unit.getX() + ((float) StrictMath.cos(facingRad) * areaOfEffect);
		final float y = unit.getY() + ((float) StrictMath.sin(facingRad) * areaOfEffect);
		for (final CUnit lastSummon : this.lastSummonUnits) {
			if (!lastSummon.isDead()) {
				lastSummon.kill(simulation);
			}
		}
		this.lastSummonUnits.clear();
		for (int i = 0; i < summonUnitCount; i++) {
			final CUnit summonedUnit = simulation.createUnitSimple(summonUnitId, unit.getPlayerIndex(), x, y, facing);
			summonedUnit.addClassification(CUnitClassification.SUMMONED);
			summonedUnit.add(simulation,
					new CBuffTimedLife(simulation.getHandleIdAllocator().createId(), buffId, getDuration(), false));
			simulation.createTemporarySpellEffectOnUnit(summonedUnit, getAlias(), CEffectType.TARGET);
			this.lastSummonUnits.add(summonedUnit);
		}
		return false;
	}

	public War3ID getSummonUnitId() {
		return summonUnitId;
	}

	public int getSummonUnitCount() {
		return summonUnitCount;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public float getAreaOfEffect() {
		return areaOfEffect;
	}

	public void setSummonUnitId(final War3ID summonUnitId) {
		this.summonUnitId = summonUnitId;
	}

	public void setSummonUnitCount(final int summonUnitCount) {
		this.summonUnitCount = summonUnitCount;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}

	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

}
