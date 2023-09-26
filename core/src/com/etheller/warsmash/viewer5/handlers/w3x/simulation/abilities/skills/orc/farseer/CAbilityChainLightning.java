package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
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

public class CAbilityChainLightning extends CAbilityTargetSpellBase {
	private static final float SECONDS_BETWEEN_JUMPS = 0.25f;
	private static final float BOLT_LIFETIME_SECONDS = 2.00f;
	private War3ID lightningIdPrimary;
	private War3ID lightningIdSecondary;
	private float damagePerTarget;
	private float damageReductionPerTarget;
	private int numberOfTargetsHit;
	private float areaOfEffect;

	public CAbilityChainLightning(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.manaburn;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.lightningIdPrimary = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 0);
		this.lightningIdSecondary = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 1);
		this.damagePerTarget = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.damageReductionPerTarget = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		this.numberOfTargetsHit = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_B + level, 0);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final SimulationRenderComponentLightning lightning = simulation.createLightning(caster, lightningIdPrimary,
					targetUnit);
			simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
			final int jumpDelayEndTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(SECONDS_BETWEEN_JUMPS / WarsmashConstants.SIMULATION_STEP_TIME);
			final int boltLifetimeEndTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(BOLT_LIFETIME_SECONDS / WarsmashConstants.SIMULATION_STEP_TIME);
			targetUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
					CWeaponSoundTypeJass.WHOKNOWS.name(), damagePerTarget);
			final float remainingDamageJumpMultiplier = 1.0f - damageReductionPerTarget;
			final Set<CUnit> previousTargets = new HashSet<>();
			previousTargets.add(targetUnit);
			simulation.registerEffect(new CEffectChainLightningBolt(boltLifetimeEndTick, jumpDelayEndTick,
					damagePerTarget * remainingDamageJumpMultiplier, remainingDamageJumpMultiplier, caster, targetUnit,
					lightning, getAlias(), lightningIdSecondary, getTargetsAllowed(), this.areaOfEffect,
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

		public CEffectChainLightningBolt(final int boltLifetimeEndTick, final int jumpDelayEndTick,
				final float remainingDamage, final float remainingDamageJumpMultiplier, final CUnit caster,
				final CUnit targetUnit, final SimulationRenderComponentLightning boltFx, final War3ID abilityId,
				final War3ID jumpLightningId, final EnumSet<CTargetType> jumpTargetsAllowed, final float jumpRadius,
				final int remainingJumps, final Set<CUnit> previousTargets) {
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
		public boolean update(final CSimulation game) {
			final int gameTurnTick = game.getGameTurnTick();
			if ((gameTurnTick >= jumpDelayEndTick) && (remainingJumps > 0)) {
				final List<CUnit> possibleJumpTargets = new ArrayList<>();
				game.getWorldCollision().enumUnitsInRange(targetUnit.getX(), targetUnit.getY(), jumpRadius,
						(enumUnit) -> {
							if (enumUnit.canBeTargetedBy(game, caster, jumpTargetsAllowed)
									&& !previousTargets.contains(enumUnit)) {
								possibleJumpTargets.add(enumUnit);
							}
							return false;
						});
				if (!possibleJumpTargets.isEmpty()) {
					final CUnit nextJumpTarget = possibleJumpTargets
							.get(game.getSeededRandom().nextInt(possibleJumpTargets.size()));

					final SimulationRenderComponentLightning lightning = game.createLightning(targetUnit,
							jumpLightningId, nextJumpTarget);
					game.createTemporarySpellEffectOnUnit(nextJumpTarget, abilityId, CEffectType.TARGET);
					final int jumpDelayEndTick = gameTurnTick
							+ (int) StrictMath.ceil(SECONDS_BETWEEN_JUMPS / WarsmashConstants.SIMULATION_STEP_TIME);
					final int boltLifetimeEndTick = gameTurnTick
							+ (int) StrictMath.ceil(BOLT_LIFETIME_SECONDS / WarsmashConstants.SIMULATION_STEP_TIME);
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
			final boolean done = gameTurnTick >= boltLifetimeEndTick;
			if (done) {
				boltFx.remove();
			}
			return done;
		}
	}
}
