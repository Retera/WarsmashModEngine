package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import java.util.Random;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityBlizzard extends CAbilityPointTargetSpellBase {
	private float buildingReduction;
	private float damage;
	private float damagePerSecond;
	private float maximumDamagePerWave;
	private int shardCount;
	private int waveCount;
	private float waveDelay;
	private float areaOfEffect;
	private War3ID effectId;

	private int currentWave;
	private int nextWaveTick;

	public CAbilityBlizzard(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final MutableGameObject worldEditorAbility, final int level) {
		buildingReduction = worldEditorAbility.getFieldAsFloat(AbilityFields.BLIZZARD_BUILDING_REDUCTION, level);
		damage = worldEditorAbility.getFieldAsFloat(AbilityFields.BLIZZARD_DAMAGE, level);
		damagePerSecond = worldEditorAbility.getFieldAsFloat(AbilityFields.BLIZZARD_DAMAGE_PER_SECOND, level);
		maximumDamagePerWave = worldEditorAbility.getFieldAsFloat(AbilityFields.BLIZZARD_MAX_DAMAGE_PER_WAVE, level);
		shardCount = worldEditorAbility.getFieldAsInteger(AbilityFields.BLIZZARD_SHARD_COUNT, level);
		waveCount = worldEditorAbility.getFieldAsInteger(AbilityFields.BLIZZARD_WAVE_COUNT, level);

		waveDelay = getCastingTime();
		setCastingTime(0); // dont use the casting time field normally
		areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
		effectId = AbstractCAbilityTypeDefinition.getEffectId(worldEditorAbility, level);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.blizzard;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		currentWave = 0;
		nextWaveTick = game.getGameTurnTick()
				+ (int) StrictMath.ceil(waveDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		return super.begin(game, caster, orderId, point);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		if (simulation.getGameTurnTick() >= nextWaveTick) {
			final Random seededRandom = simulation.getSeededRandom();
			for (int i = 0; i < shardCount; i++) {
				final float randomAngle = seededRandom.nextFloat((float) (StrictMath.PI * 2));
//				float randomDistance = (float)(1 - StrictMath.pow(seededRandom.nextFloat(), 2)) * ;
			}
//			simulation.spawnSpellEffectOnPoint(x, y, facing, alias, effectType, index);
			currentWave++;
			nextWaveTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(waveDelay / WarsmashConstants.SIMULATION_STEP_TIME);
		}
		return currentWave < waveCount;
	}

}
