package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

public class CAbilityBladeStorm extends CAbilityNoTargetSpellBase {

    // Dégâts infligés par seconde aux ennemis dans la zone
    private float damagePerSecond;
    // Rayon de la tornade
    private float areaOfEffect;
    // Durée totale du tourbillon en secondes
    private float duration;

    public CAbilityBladeStorm(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    // -------------------------------------------------------------------------
    // Données SLK
    // -------------------------------------------------------------------------

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.damagePerSecond = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        this.areaOfEffect    = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
        this.duration        = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.bladestorm;
    }

    // -------------------------------------------------------------------------
    // Déclenchement du sort
    // -------------------------------------------------------------------------

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {

        // --------------------------------------------------------------------
        // 1. Rendre le Blademaster invulnérable pendant le tourbillon
        // --------------------------------------------------------------------
        caster.setInvulnerable(true);

        // --------------------------------------------------------------------
        // 2. Immobiliser le Blademaster : STUN pour bloquer les ordres,
        //    DISABLE_SPELLS pour empêcher tout autre sort pendant le canal.
        //    On conserve les références pour le retrait propre à la fin.
        // --------------------------------------------------------------------
        final StateModBuff stunBuff         = new StateModBuff(StateModBuffType.STUN,           getHandleId());
        final StateModBuff disableSpellBuff = new StateModBuff(StateModBuffType.DISABLE_SPELLS, getHandleId());

        caster.addStateModBuff(stunBuff);
        caster.addStateModBuff(disableSpellBuff);
        caster.computeUnitState(simulation, StateModBuffType.STUN);
        caster.computeUnitState(simulation, StateModBuffType.DISABLE_SPELLS);

        // --------------------------------------------------------------------
        // 3. Effet visuel de tourbillon sur le caster
        // --------------------------------------------------------------------
        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);

        // --------------------------------------------------------------------
        // 4. Calcul du tick de fin et des dégâts par tick de simulation
        // --------------------------------------------------------------------
        final int endTick = simulation.getGameTurnTick()
                + (int) StrictMath.ceil(this.duration / WarsmashConstants.SIMULATION_STEP_TIME);

        final float damagePerTick = this.damagePerSecond * WarsmashConstants.SIMULATION_STEP_TIME;

        // --------------------------------------------------------------------
        // 5. Enregistrement de l'effet temporisé qui gère les ticks de dégâts
        // --------------------------------------------------------------------
        simulation.registerEffect(new CEffectBladestorm(
                caster,
                endTick,
                damagePerTick,
                this.areaOfEffect,
                getTargetsAllowed(),
                stunBuff,
                disableSpellBuff,
                getAlias()
        ));

        return false;
    }

    // =========================================================================
    // Effet interne : gère chaque tick de dégâts et le nettoyage final
    // =========================================================================

    private static final class CEffectBladestorm implements CEffect {

        private final CUnit                                   caster;
        private final int                                     endTick;
        private final float                                   damagePerTick;
        private final float                                   areaOfEffect;
        private final java.util.EnumSet<com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType>
                targetsAllowed;
        private final StateModBuff                            stunBuff;
        private final StateModBuff                            disableSpellBuff;
        private final War3ID                                  alias;

        // Drapeau pour s'assurer qu'on ne nettoie qu'une seule fois
        private boolean cleaned;

        public CEffectBladestorm(
                final CUnit                                   caster,
                final int                                     endTick,
                final float                                   damagePerTick,
                final float                                   areaOfEffect,
                final java.util.EnumSet<com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType>
                        targetsAllowed,
                final StateModBuff                            stunBuff,
                final StateModBuff                            disableSpellBuff,
                final War3ID                                  alias) {
            this.caster          = caster;
            this.endTick         = endTick;
            this.damagePerTick   = damagePerTick;
            this.areaOfEffect    = areaOfEffect;
            this.targetsAllowed  = targetsAllowed;
            this.stunBuff        = stunBuff;
            this.disableSpellBuff = disableSpellBuff;
            this.alias           = alias;
            this.cleaned         = false;
        }

        @Override
        public boolean update(final CSimulation game) {
            if (this.cleaned) {
                return true;
            }

            final boolean expired    = game.getGameTurnTick() >= this.endTick;
            final boolean casterDead = this.caster.isDead();

            if (casterDead) {
                // Le Blademaster est mort : on nettoie sans faire de dégâts
                removeEffects(game);
                return true;
            }

            // ----------------------------------------------------------------
            // Tick de dégâts : toutes les unités ennemies vivantes dans la zone
            // ----------------------------------------------------------------
            game.getWorldCollision().enumUnitsInRange(
                    this.caster.getX(),
                    this.caster.getY(),
                    this.areaOfEffect,
                    enumUnit -> {
                        if (!enumUnit.isDead()
                                && !enumUnit.isUnitAlly(game.getPlayer(this.caster.getPlayerIndex()))
                                && enumUnit.canBeTargetedBy(game, this.caster, this.targetsAllowed)) {

                            enumUnit.damage(
                                    game,
                                    this.caster,
                                    false,                               // pas une attaque standard
                                    false,                               // non ranged
                                    CAttackType.SPELLS,
                                    CDamageType.UNIVERSAL,
                                    CWeaponSoundTypeJass.WHOKNOWS.name(),
                                    this.damagePerTick
                            );

                            // Petit effet visuel sur chaque cible touchée
                            game.createTemporarySpellEffectOnUnit(enumUnit, this.alias, CEffectType.TARGET);
                        }
                        return false;
                    });

            if (expired) {
                removeEffects(game);
                return true;
            }

            return false;
        }

        // --------------------------------------------------------------------
        // Nettoyage : retire l'invulnérabilité et les états du caster
        // --------------------------------------------------------------------
        private void removeEffects(final CSimulation game) {
            this.cleaned = true;

            if (!this.caster.isDead()) {
                this.caster.setInvulnerable(false);

                this.caster.removeStateModBuff(this.stunBuff);
                this.caster.removeStateModBuff(this.disableSpellBuff);
                this.caster.computeUnitState(game, StateModBuffType.STUN);
                this.caster.computeUnitState(game, StateModBuffType.DISABLE_SPELLS);
            }
        }
    }
}