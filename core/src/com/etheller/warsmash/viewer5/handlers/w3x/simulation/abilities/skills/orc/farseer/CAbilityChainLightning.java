package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;

import java.util.*;

public class CAbilityChainLightning extends CAbilityTargetSpellBase {
	private static final float SECONDS_BETWEEN_JUMPS = 0.25f;
	private static final float BOLT_LIFETIME_SECONDS = 2.00f;
	private War3ID lightningIdPrimary;
	private War3ID lightningIdSecondary;
	private float damagePerTarget;
	private float damageReductionPerTarget;
	private int numberOfTargetsHit;
	private float areaOfEffect;

	public CAbilityChainLightning(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.manaburn;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.lightningIdPrimary = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 0);
		this.lightningIdSecondary = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 1);
		this.damagePerTarget = worldEditorAbility.getFieldAsFloat(AbilityFields.ChainLightning.DAMAGE_PER_TARGET,
				level);
		this.damageReductionPerTarget =
				worldEditorAbility.getFieldAsFloat(AbilityFields.ChainLightning.DAMAGE_REDUCTION_PER_TARGET, level);
		this.numberOfTargetsHit =
				worldEditorAbility.getFieldAsInteger(AbilityFields.ChainLightning.NUMBER_OF_TARGETS_HIT, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			SimulationRenderComponentLightning lightning = simulation.createLightning(caster, lightningIdPrimary,
					targetUnit);
			simulation.createSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
			int jumpDelayEndTick =
					simulation.getGameTurnTick() + (int) StrictMath.ceil(SECONDS_BETWEEN_JUMPS / WarsmashConstants.SIMULATION_STEP_TIME);
			int boltLifetimeEndTick =
					simulation.getGameTurnTick() + (int) StrictMath.ceil(BOLT_LIFETIME_SECONDS / WarsmashConstants.SIMULATION_STEP_TIME);
			targetUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
					CWeaponSoundTypeJass.WHOKNOWS.name(), damagePerTarget);
			float remainingDamageJumpMultiplier = 1.0f - damageReductionPerTarget;
			Set<CUnit> previousTargets = new HashSet<>();
			previousTargets.add(targetUnit);
			simulation.registerEffect(new CEffectChainLightningBolt(boltLifetimeEndTick, jumpDelayEndTick,
					damagePerTarget * remainingDamageJumpMultiplier, remainingDamageJumpMultiplier, caster, targetUnit
					, lightning, getAlias(), lightningIdSecondary, getTargetsAllowed(), this.areaOfEffect,
					this.numberOfTargetsHit - 1, previousTargets));
		}
		return false;
	}

	private static final class CEffectChainLightningBolt implements CEffect {

		private final int boltLifetimeEndTick;
		private final float remainingDamage;
		private final float remainingDamageJumpMultiplier;
		private final CUnit caster;
		private final CUnit targetUnit;
		private final SimulationRenderComponentLightning boltFx;
		private final War3ID abilityId;
		private final War3ID jumpLightningId;
		private final EnumSet<CTargetType> jumpTargetsAllowed;
		private final float jumpRadius;
		private final int remainingJumps;
		private int jumpDelayEndTick;

		private final Set<CUnit> previousTargets;

		public CEffectChainLightningBolt(int boltLifetimeEndTick, int jumpDelayEndTick, float remainingDamage,
										 float remainingDamageJumpMultiplier, CUnit caster, CUnit targetUnit,
										 SimulationRenderComponentLightning boltFx, War3ID abilityId,
										 War3ID jumpLightningId, EnumSet<CTargetType> jumpTargetsAllowed,
										 float jumpRadius, int remainingJumps, Set<CUnit> previousTargets) {
			this.boltLifetimeEndTick = boltLifetimeEndTick;
			this.jumpDelayEndTick = jumpDelayEndTick;
			this.remainingDamage = remainingDamage;
			this.remainingDamageJumpMultiplier = remainingDamageJumpMultiplier;
			this.caster = caster;
			this.targetUnit = targetUnit;
			this.boltFx = boltFx;
			this.abilityId = abilityId;
			this.jumpLightningId = jumpLightningId;
			this.jumpTargetsAllowed = jumpTargetsAllowed;
			this.jumpRadius = jumpRadius;
			this.remainingJumps = remainingJumps;
			this.previousTargets = previousTargets;
		}

		@Override
		public boolean update(CSimulation game) {
			int gameTurnTick = game.getGameTurnTick();
			if (gameTurnTick >= jumpDelayEndTick && remainingJumps > 0) {
				List<CUnit> possibleJumpTargets = new ArrayList<>();
				game.getWorldCollision().enumUnitsInRange(targetUnit.getX(), targetUnit.getY(), jumpRadius,
						(enumUnit) -> {
					if (enumUnit.canBeTargetedBy(game, caster, jumpTargetsAllowed) && !previousTargets.contains(enumUnit)) {
						possibleJumpTargets.add(enumUnit);
					}
					return false;
				});
				if (!possibleJumpTargets.isEmpty()) {
					CUnit nextJumpTarget =
							possibleJumpTargets.get(game.getSeededRandom().nextInt(possibleJumpTargets.size()));

					SimulationRenderComponentLightning lightning = game.createLightning(targetUnit, jumpLightningId,
							nextJumpTarget);
					game.createSpellEffectOnUnit(nextJumpTarget, abilityId, CEffectType.TARGET);
					int jumpDelayEndTick =
							gameTurnTick + (int) StrictMath.ceil(SECONDS_BETWEEN_JUMPS / WarsmashConstants.SIMULATION_STEP_TIME);
					int boltLifetimeEndTick =
							gameTurnTick + (int) StrictMath.ceil(BOLT_LIFETIME_SECONDS / WarsmashConstants.SIMULATION_STEP_TIME);
					nextJumpTarget.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
							CWeaponSoundTypeJass.WHOKNOWS.name(), remainingDamage);
					previousTargets.add(nextJumpTarget);
					game.registerEffect(new CEffectChainLightningBolt(boltLifetimeEndTick, jumpDelayEndTick,
							remainingDamage * remainingDamageJumpMultiplier, remainingDamageJumpMultiplier, caster,
							nextJumpTarget, lightning, abilityId, jumpLightningId, jumpTargetsAllowed, jumpRadius,
							remainingJumps - 1, previousTargets));
				}
				this.jumpDelayEndTick = Integer.MAX_VALUE;
			}
			boolean done = gameTurnTick >= boltLifetimeEndTick;
			if (done) {
				boltFx.remove();
			}
			return done;
		}
	}
}
