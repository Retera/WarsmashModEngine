package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
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

public class CAbilityItemFigurineSummon extends CAbilityNoTargetSpellBase {
	private War3ID summonUnitId;
	private int summonUnitCount;
	private War3ID summonUnit2Id;
	private int summonUnit2Count;
	private War3ID buffId;
	private float duration;
	private float areaOfEffect;

	public CAbilityItemFigurineSummon(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final MutableGameObject worldEditorAbility, final int level) {
		this.summonUnitId = War3ID
				.fromString(worldEditorAbility.getFieldAsString(AbilityFields.ITEM_FIGURINE_SUMMON_UNIT_TYPE_1, level));
		this.summonUnitCount = worldEditorAbility.getFieldAsInteger(AbilityFields.ITEM_FIGURINE_SUMMON_UNIT_COUNT_1,
				level);
		this.summonUnit2Id = War3ID
				.fromString(worldEditorAbility.getFieldAsString(AbilityFields.ITEM_FIGURINE_SUMMON_UNIT_TYPE_2, level));
		this.summonUnit2Count = worldEditorAbility.getFieldAsInteger(AbilityFields.ITEM_FIGURINE_SUMMON_UNIT_COUNT_2,
				level);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.itemfigurinesummon;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final float facing = unit.getFacing();
		final float facingRad = (float) StrictMath.toRadians(facing);
		final float x = unit.getX() + ((float) StrictMath.cos(facingRad) * areaOfEffect);
		final float y = unit.getY() + ((float) StrictMath.sin(facingRad) * areaOfEffect);
		for (int i = 0; i < summonUnitCount; i++) {
			final CUnit summonedUnit = simulation.createUnitSimple(summonUnitId, unit.getPlayerIndex(), x, y, facing);
			summonedUnit.addClassification(CUnitClassification.SUMMONED);
			summonedUnit.add(simulation,
					new CBuffTimedLife(simulation.getHandleIdAllocator().createId(), buffId, duration));
		}
		for (int i = 0; i < summonUnit2Count; i++) {
			final CUnit summonedUnit = simulation.createUnitSimple(summonUnit2Id, unit.getPlayerIndex(), x, y, facing);
			summonedUnit.addClassification(CUnitClassification.SUMMONED);
			summonedUnit.add(simulation,
					new CBuffTimedLife(simulation.getHandleIdAllocator().createId(), buffId, duration));
		}
		return false;
	}

	public War3ID getSummonUnitId() {
		return summonUnitId;
	}

	public int getSummonUnitCount() {
		return summonUnitCount;
	}

	public War3ID getSummonUnit2Id() {
		return summonUnit2Id;
	}

	public int getSummonUnit2Count() {
		return summonUnit2Count;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public float getDuration() {
		return duration;
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

	public void setSummonUnit2Id(final War3ID summonUnit2Id) {
		this.summonUnit2Id = summonUnit2Id;
	}

	public void setSummonUnit2Count(final int summonUnit2Count) {
		this.summonUnit2Count = summonUnit2Count;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}

	public void setDuration(final float duration) {
		this.duration = duration;
	}

	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

}
