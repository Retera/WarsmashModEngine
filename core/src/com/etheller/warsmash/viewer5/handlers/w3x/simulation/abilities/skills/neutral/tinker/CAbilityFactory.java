package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPassiveSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityFactory extends CAbilityPassiveSpellBase {
	private War3ID spawnUnitId;
	private float leashRange;
	private float spawnInterval;
	private War3ID buffId;

	private int lastSpawnTick;

	public CAbilityFactory(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.spawnUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0));
		this.leashRange = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		this.spawnInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public void onTick(final CSimulation game, final CUnit factory) {
		final int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= (lastSpawnTick
				+ (int) (StrictMath.ceil(this.spawnInterval / WarsmashConstants.SIMULATION_STEP_TIME)))) {

			final float facing = factory.getFacing();
			final float facingRad = (float) StrictMath.toRadians(facing);
			final float x = factory.getX() + ((float) StrictMath.cos(facingRad) * getAreaOfEffect());
			final float y = factory.getY() + ((float) StrictMath.sin(facingRad) * getAreaOfEffect());

			final CUnit spawnedUnit = game.createUnitSimple(this.spawnUnitId, factory.getPlayerIndex(), x, y, facing);
			game.unitSoundEffectEvent(factory, getAlias());
			spawnedUnit.addClassification(CUnitClassification.SUMMONED);
			spawnedUnit.add(game,
					new CBuffTimedLife(game.getHandleIdAllocator().createId(), this.buffId, getDuration(), false));
			final AbilityTarget rallyPoint = factory.getRallyPoint();
			if (rallyPoint != null) {
				spawnedUnit.order(game, OrderIds.smart, rallyPoint);
			}
			lastSpawnTick = gameTurnTick;
		}
	}

	public War3ID getSpawnUnitId() {
		return spawnUnitId;
	}

	public void setSpawnUnitId(final War3ID spawnUnitId) {
		this.spawnUnitId = spawnUnitId;
	}

	public float getLeashRange() {
		return leashRange;
	}

	public void setLeashRange(final float leashRange) {
		this.leashRange = leashRange;
	}

	public float getSpawnInterval() {
		return spawnInterval;
	}

	public void setSpawnInterval(final float spawnInterval) {
		this.spawnInterval = spawnInterval;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}
}
