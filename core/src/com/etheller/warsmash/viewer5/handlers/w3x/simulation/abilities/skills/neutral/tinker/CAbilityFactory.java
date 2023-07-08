package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import com.etheller.warsmash.units.manager.MutableObjectData;
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

	public CAbilityFactory(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.spawnUnitId = War3ID.fromString(worldEditorAbility.getFieldAsString(AbilityFields.Factory.SPAWN_UNIT_ID,
				level));
		this.leashRange = worldEditorAbility.getFieldAsFloat(AbilityFields.Factory.LEASH_RANGE, level);
		this.spawnInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.Factory.SPAWN_INTERVAL, level);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public void onTick(CSimulation game, CUnit factory) {
		int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= lastSpawnTick + (int) (StrictMath.ceil(this.spawnInterval / WarsmashConstants.SIMULATION_STEP_TIME))) {

			float facing = factory.getFacing();
			float facingRad = (float) StrictMath.toRadians(facing);
			float x = factory.getX() + ((float) StrictMath.cos(facingRad) * getAreaOfEffect());
			float y = factory.getY() + ((float) StrictMath.sin(facingRad) * getAreaOfEffect());

			CUnit spawnedUnit = game.createUnitSimple(this.spawnUnitId, factory.getPlayerIndex(), x, y, facing);
			game.unitSoundEffectEvent(factory, getAlias());
			spawnedUnit.addClassification(CUnitClassification.SUMMONED);
			spawnedUnit.add(game, new CBuffTimedLife(game.getHandleIdAllocator().createId(), this.buffId,
					getDuration()));
			AbilityTarget rallyPoint = factory.getRallyPoint();
			if(rallyPoint != null) {
				spawnedUnit.order(game, OrderIds.smart, rallyPoint);
			}
			lastSpawnTick = gameTurnTick;
		}
	}

	public War3ID getSpawnUnitId() {
		return spawnUnitId;
	}

	public void setSpawnUnitId(War3ID spawnUnitId) {
		this.spawnUnitId = spawnUnitId;
	}

	public float getLeashRange() {
		return leashRange;
	}

	public void setLeashRange(float leashRange) {
		this.leashRange = leashRange;
	}

	public float getSpawnInterval() {
		return spawnInterval;
	}

	public void setSpawnInterval(float spawnInterval) {
		this.spawnInterval = spawnInterval;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(War3ID buffId) {
		this.buffId = buffId;
	}
}
