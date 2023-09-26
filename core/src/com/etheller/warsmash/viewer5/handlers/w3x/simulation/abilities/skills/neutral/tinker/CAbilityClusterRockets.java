package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class CAbilityClusterRockets extends CAbilityPointTargetSpellBase {
	private static final float MISSILE_ARRIVAL_DELAY = 1.0f;
	private static final float PER_ROCKET_DELAY = 0.01f;
	private final Rectangle recycleRect = new Rectangle();
	private float buildingReduction;
	private float damage;
	private float maximumDamagePerWave;
	private int missileCount;
	private float damageInterval;
	private float effectDuration;
	private float areaOfEffect;
	private int currentWave;
	private int nextWaveTick;
	private int nextMissileTick;
	private int currentMissile;
	private int effectDurationEndTick;
	private int missileLaunchingEndTick;
	private int missileLaunchDurationTicks;
	private War3ID buffId;

	public CAbilityClusterRockets(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.buildingReduction = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);
		this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.maximumDamagePerWave = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
		this.missileCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_C + level, 0);

		this.damageInterval = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		this.effectDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_F + level, 0);
		setCastingTime(0); // dont use the casting time field normally
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.clusterrockets;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick();
		this.currentWave = 0;
		this.currentMissile = 0;
		final int missileTravelTicks = (int) StrictMath
				.ceil(MISSILE_ARRIVAL_DELAY / WarsmashConstants.SIMULATION_STEP_TIME);
		int durationTicks = (int) StrictMath.ceil(effectDuration / WarsmashConstants.SIMULATION_STEP_TIME);
		this.nextWaveTick = gameTurnTick + missileTravelTicks;
		this.nextMissileTick = gameTurnTick;
		this.effectDurationEndTick = nextWaveTick + durationTicks;

		final int expectedMissileCount = (int) StrictMath.ceil((effectDuration) / PER_ROCKET_DELAY);
		missileLaunchingEndTick = gameTurnTick + durationTicks;
		if (expectedMissileCount > this.missileCount) {
			final int shortenedDurationTicks = this.missileCount
					* (int) StrictMath.ceil(PER_ROCKET_DELAY / WarsmashConstants.SIMULATION_STEP_TIME);
			missileLaunchingEndTick = gameTurnTick + shortenedDurationTicks;
			durationTicks = shortenedDurationTicks;
		}
		missileLaunchDurationTicks = durationTicks;
		return true;
	}

	@Override
	public boolean doChannelTick(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		final int gameTurnTick = simulation.getGameTurnTick();
		if ((gameTurnTick >= this.nextMissileTick) && (this.currentMissile < this.missileCount)) {
			final Random seededRandom = simulation.getSeededRandom();
			final float elapsedTimeRatio = 1.0f
					- ((missileLaunchingEndTick - gameTurnTick) / (float) missileLaunchDurationTicks);
			final float targetingAngle = (float) ((unit.angleTo(target) + StrictMath.PI)
					- (elapsedTimeRatio * (StrictMath.PI * 2)));

			final float targetingX = target.getX() + ((float) StrictMath.cos(targetingAngle) * areaOfEffect * 0.5f);
			final float targetingY = target.getY() + ((float) StrictMath.sin(targetingAngle) * areaOfEffect * 0.5f);
			final float randomAngle = seededRandom.nextFloat((float) (StrictMath.PI * 2));
			final float randomDistance = seededRandom.nextFloat() * this.areaOfEffect * 0.25f;
			final float missileLandX = targetingX + ((float) StrictMath.cos(randomAngle) * randomDistance);
			final float missileLandY = targetingY + ((float) StrictMath.sin(randomAngle) * randomDistance);
			final AbilityPointTarget missileLandPoint = new AbilityPointTarget(missileLandX, missileLandY);
			final double angleToLandPoint = unit.angleTo(missileLandPoint);
			final double distance = unit.distance(missileLandPoint);
			double speed = distance / MISSILE_ARRIVAL_DELAY;
			if (speed < simulation.getGameplayConstants().getMinUnitSpeed()) {
				speed = simulation.getGameplayConstants().getMinUnitSpeed();
			}
			simulation.createProjectile(unit, getAlias(), unit.getX(), unit.getY(), (float) angleToLandPoint,
					(float) speed, false, missileLandPoint, CAbilityProjectileListener.DO_NOTHING);
			this.nextMissileTick = gameTurnTick
					+ (int) StrictMath.ceil(PER_ROCKET_DELAY / WarsmashConstants.SIMULATION_STEP_TIME);
			currentMissile++;
		}
		if (gameTurnTick >= this.nextWaveTick) {
			this.currentWave++;
			final List<CUnit> damageTargets = new ArrayList<>();
			simulation.getWorldCollision()
					.enumUnitsInRect(this.recycleRect.set(target.getX() - this.areaOfEffect,
							target.getY() - this.areaOfEffect, this.areaOfEffect * 2, this.areaOfEffect * 2),
							new CUnitEnumFunction() {
								@Override
								public boolean call(final CUnit possibleTarget) {
									if (possibleTarget.canReach(target, CAbilityClusterRockets.this.areaOfEffect)
											&& possibleTarget.canBeTargetedBy(simulation, unit, getTargetsAllowed())) {
										damageTargets.add(possibleTarget);
									}
									return false;
								}
							});
			if (currentWave == 1) {
				// stun
				for (final CUnit damageTarget : damageTargets) {
					damageTarget.add(simulation, new CBuffStun(simulation.getHandleIdAllocator().createId(), buffId,
							getDurationForTarget(damageTarget)));
				}
			}
			else {
				float damagePerTarget = this.damage;
				if ((damagePerTarget * damageTargets.size()) > maximumDamagePerWave) {
					damagePerTarget = maximumDamagePerWave / damageTargets.size();
				}
				final float damagePerTargetBuilding = damagePerTarget * (buildingReduction);
				for (final CUnit damageTarget : damageTargets) {
					float thisTargetDamage;
					if (damageTarget.isBuilding()) {
						thisTargetDamage = damagePerTargetBuilding;
					}
					else {
						thisTargetDamage = damagePerTarget;
					}
					damageTarget.damage(simulation, unit, false, true, CAttackType.SPELLS, CDamageType.FIRE,
							CWeaponSoundTypeJass.WHOKNOWS.name(), thisTargetDamage);
				}
			}
			this.nextWaveTick = gameTurnTick
					+ (int) StrictMath.ceil(damageInterval / WarsmashConstants.SIMULATION_STEP_TIME);
		}
		return gameTurnTick < effectDurationEndTick;
	}

	@Override
	public float getUIAreaOfEffect() {
		return this.areaOfEffect;
	}

}
