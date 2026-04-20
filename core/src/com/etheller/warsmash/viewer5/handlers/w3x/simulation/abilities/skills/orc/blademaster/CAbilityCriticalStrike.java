package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityCriticalStrike extends CAbilityNoTargetSpellBase {

    // Probabilité de déclencher un coup critique (0.0 à 1.0)
    private float chance;
    // Multiplicateur de dégâts appliqué lors d'un coup critique (ex : 2.0 = ×2)
    private float damageMultiplier;

    // Référence au listener pour pouvoir le retirer proprement dans onRemove
    private CriticalStrikeListener criticalStrikeListener;

    public CAbilityCriticalStrike(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    // -------------------------------------------------------------------------
    // Données SLK
    // -------------------------------------------------------------------------

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        // DataA = chance (décimal, ex : 0.15 pour 15 %)
        this.chance           = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        // DataB = multiplicateur de dégâts (ex : 2.0 pour ×2)
        this.damageMultiplier = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
    }

    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        // Passive : on ne crée pas de CBehaviorNoTargetSpellBase (super.onAdd non appelé)
        this.criticalStrikeListener = new CriticalStrikeListener(
                this.chance,
                this.damageMultiplier,
                getAlias()
        );
        unit.addPostDamageListener(this.criticalStrikeListener);
    }

    @Override
    public void onRemove(final CSimulation game, final CUnit unit) {
        if (this.criticalStrikeListener != null) {
            unit.removePostDamageListener(this.criticalStrikeListener);
            this.criticalStrikeListener = null;
        }
    }

    // -------------------------------------------------------------------------
    // Passive : aucune activation manuelle possible
    // -------------------------------------------------------------------------

    @Override
    public int getBaseOrderId() {
        // À remplacer par OrderIds.criticalbash si la constante existe
        return 852532;
    }

    @Override
    protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
                                               final AbilityTargetCheckReceiver<Void> receiver) {
        receiver.orderIdNotAccepted();
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
        return false;
    }

    // =========================================================================
    // Listener interne — cœur de la mécanique de coup critique
    // =========================================================================

    private static final class CriticalStrikeListener implements CUnitAttackPostDamageListener {

        private final float  chance;
        private final float  damageMultiplier;
        private final War3ID alias;

        // Drapeau anti-récursion : empêche le dégât bonus d'un crit
        // de déclencher un second crit en chaîne
        private boolean applyingCrit;

        public CriticalStrikeListener(final float chance, final float damageMultiplier, final War3ID alias) {
            this.chance           = chance;
            this.damageMultiplier = damageMultiplier;
            this.alias            = alias;
            this.applyingCrit     = false;
        }

        @Override
        public void onHit(
                final CSimulation simulation,
                final CUnit source,
                final AbilityTarget target,
                final float damage) {

            // Anti-récursion : on n'applique pas de crit sur le dégât bonus lui-même
            if (this.applyingCrit) {
                return;
            }

            // Récupération de l'unité cible depuis l'AbilityTarget
            final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
            if (targetUnit == null || targetUnit.isDead()) {
                return;
            }

            // Tirage aléatoire déterministe (seed gérée par la simulation)
            if (simulation.getSeededRandom().nextFloat() < this.chance) {
                this.applyingCrit = true;

                // Dégâts bonus = (multiplicateur - 1) × dégâts de base
                // Ex : multiplicateur 2.0 avec 100 dégâts → 100 bonus → 200 total
                final float bonusCritDamage = damage * (this.damageMultiplier - 1.0f);

                targetUnit.damage(
                        simulation,
                        source,
                        false,                                // pas une 2e attaque standard
                        false,                                // non ranged
                        CAttackType.NORMAL,
                        CDamageType.NORMAL,
                        CWeaponSoundTypeJass.WHOKNOWS.name(),
                        bonusCritDamage
                );

                // Effet visuel sur la cible au moment du coup critique
                simulation.createTemporarySpellEffectOnUnit(targetUnit, this.alias, CEffectType.TARGET);

                this.applyingCrit = false;
            }
        }
    }
}