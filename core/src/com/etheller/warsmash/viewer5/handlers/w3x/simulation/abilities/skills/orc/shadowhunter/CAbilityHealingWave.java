package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.shadowhunter;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;

import java.util.HashSet;
import java.util.Set;

public class CAbilityHealingWave extends CAbilityTargetSpellBase {

    private float healAmount;
    private float healReductionPerTarget;
    private int numberOfTargetsHit;
    private War3ID lightningId;           // l'effet visuel de la vague (généralement "CLPB" ou "HEAL")

    public CAbilityHealingWave(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.healAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        this.numberOfTargetsHit = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_B + level, 0);
        this.healReductionPerTarget = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
        this.lightningId = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level, 0);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.healingwave;
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
        if (targetUnit != null) {
            final float firstHeal = healAmount;

            // Effet visuel + soin sur la première cible
            final SimulationRenderComponentLightning lightning = simulation.createLightning(caster, lightningId, targetUnit);
            simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);

            // Soigne la première cible
            targetUnit.heal(simulation, firstHeal);

            // Préparation pour les sauts
            final float reductionFactor = 1.0f - healReductionPerTarget;
            final Set<CUnit> previousTargets = new HashSet<>();
            previousTargets.add(targetUnit);

            // Lance l'effet de chaîne de soins
            final int jumpDelayEndTick = simulation.getGameTurnTick() + 8; // ~0.25s comme Chain Lightning
            simulation.registerEffect(new CEffectHealingWave(
                    lightning,
                    caster,
                    targetUnit,
                    firstHeal * reductionFactor,
                    reductionFactor,
                    numberOfTargetsHit - 1,
                    previousTargets,
                    lightningId,
                    getAlias(),
                    jumpDelayEndTick
            ));
        }
        return false;
    }

    // ==================== Effet interne pour gérer les sauts ====================
    private static final class CEffectHealingWave implements com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect {

        private final SimulationRenderComponentLightning currentLightning;
        private final CUnit caster;
        private final CUnit currentTarget;
        private float remainingHeal;
        private final float healReduction;
        private int remainingJumps;
        private final Set<CUnit> previousTargets;
        private final War3ID lightningId;
        private final War3ID abilityId;
        private int nextJumpTick;

        public CEffectHealingWave(SimulationRenderComponentLightning lightning, CUnit caster, CUnit currentTarget,
                                  float remainingHeal, float healReduction, int remainingJumps,
                                  Set<CUnit> previousTargets, War3ID lightningId, War3ID abilityId, int nextJumpTick) {
            this.currentLightning = lightning;
            this.caster = caster;
            this.currentTarget = currentTarget;
            this.remainingHeal = remainingHeal;
            this.healReduction = healReduction;
            this.remainingJumps = remainingJumps;
            this.previousTargets = previousTargets;
            this.lightningId = lightningId;
            this.abilityId = abilityId;
            this.nextJumpTick = nextJumpTick;
        }

        @Override
        public boolean update(final CSimulation game) {
            if (game.getGameTurnTick() < nextJumpTick || remainingJumps <= 0) {
                return false;
            }

            // Cherche la prochaine cible alliée vivante
            final CUnit nextTarget = findNextAlly(game);

            if (nextTarget != null) {
                // Crée le nouvel effet visuel
                final SimulationRenderComponentLightning newLightning = game.createLightning(currentTarget, lightningId, nextTarget);
                game.createTemporarySpellEffectOnUnit(nextTarget, abilityId, CEffectType.TARGET);

                // Soigne
                nextTarget.heal(game, remainingHeal);

                // Prépare le prochain saut
                previousTargets.add(nextTarget);
                this.remainingHeal *= healReduction;
                this.remainingJumps--;
                this.nextJumpTick = game.getGameTurnTick() + 8; // délai entre les sauts

                // Remplace l'ancien lightning par le nouveau
                if (currentLightning != null) {
                    currentLightning.remove();
                }

                // Continue la chaîne
                game.registerEffect(new CEffectHealingWave(newLightning, caster, nextTarget, remainingHeal,
                        healReduction, remainingJumps, previousTargets, lightningId, abilityId, nextJumpTick));
            } else {
                if (currentLightning != null) {
                    currentLightning.remove();
                }
            }

            return true; // on termine cet effet
        }

        private CUnit findNextAlly(final CSimulation game) {
            final float searchRadius = 500f; // rayon de recherche raisonnable

            final CUnit[] bestTarget = {null};
            final double[] bestDistance = {Float.MAX_VALUE};

            game.getWorldCollision().enumUnitsInRange(currentTarget.getX(), currentTarget.getY(), searchRadius, unit -> {
                if (unit.isDead() || !unit.isUnitAlly(game.getPlayer(caster.getPlayerIndex()))
                        || previousTargets.contains(unit)) {
                    return false;
                }

                double dist = unit.distance(currentTarget);
                if (dist < bestDistance[0]) {
                    bestDistance[0] = dist;
                    bestTarget[0] = unit;
                }
                return false;
            });

            return bestTarget[0];
        }
    }
}