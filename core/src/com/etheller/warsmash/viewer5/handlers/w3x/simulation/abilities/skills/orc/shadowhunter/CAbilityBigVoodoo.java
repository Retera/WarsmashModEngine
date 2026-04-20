package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.shadowhunter;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

import java.util.ArrayList;
import java.util.List;

public class CAbilityBigVoodoo extends CAbilityNoTargetSpellBase {

    private float areaOfEffect;
    private float duration;

    public CAbilityBigVoodoo(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
        this.duration     = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.voodoo;
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {

        // --------------------------------------------------------------------
        // 1. Collecte de toutes les unités alliées dans la zone (caster inclus
        //    car il est au centre de l'enumUnitsInRange)
        // --------------------------------------------------------------------
        final List<CUnit> affectedUnits = new ArrayList<>();

        simulation.getWorldCollision().enumUnitsInRange(
                caster.getX(), caster.getY(), this.areaOfEffect,
                enumUnit -> {
                    if (!enumUnit.isDead()
                            && enumUnit.isUnitAlly(simulation.getPlayer(caster.getPlayerIndex()))) {
                        affectedUnits.add(enumUnit);
                    }
                    return false;
                });

        // --------------------------------------------------------------------
        // 2. Rendre toutes les unités collectées invulnérables
        // --------------------------------------------------------------------
        for (final CUnit ally : affectedUnits) {
            ally.setInvulnerable(true);
        }

        // --------------------------------------------------------------------
        // 3. Immobiliser le caster pendant le rituel
        //    - STUN : bloque les ordres / mouvements (CBehaviorStun)
        //    - DISABLE_SPELLS : empêche de lancer un autre sort
        //    On conserve les références pour les retirer proprement ensuite.
        // --------------------------------------------------------------------
        final StateModBuff stunBuff         = new StateModBuff(StateModBuffType.STUN,           getHandleId());
        final StateModBuff disableSpellBuff = new StateModBuff(StateModBuffType.DISABLE_SPELLS, getHandleId());

        caster.addStateModBuff(stunBuff);
        caster.addStateModBuff(disableSpellBuff);
        caster.computeUnitState(simulation, StateModBuffType.STUN);
        caster.computeUnitState(simulation, StateModBuffType.DISABLE_SPELLS);

        // --------------------------------------------------------------------
        // 4. Effet visuel sur le caster
        // --------------------------------------------------------------------
        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);

        // --------------------------------------------------------------------
        // 5. Enregistrement de l'effet temporisé pour nettoyer à la fin
        // --------------------------------------------------------------------
        final int endTick = simulation.getGameTurnTick()
                + (int) StrictMath.ceil(this.duration / WarsmashConstants.SIMULATION_STEP_TIME);

        simulation.registerEffect(new CEffectBigBadVoodoo(
                caster, affectedUnits, stunBuff, disableSpellBuff, endTick));

        return false;
    }

    // ========================================================================
    // Effet interne : gère la durée du rituel et le nettoyage final
    // ========================================================================
    private static final class CEffectBigBadVoodoo implements CEffect {

        private final CUnit          caster;
        private final List<CUnit>    affectedUnits;
        private final StateModBuff   stunBuff;
        private final StateModBuff   disableSpellBuff;
        private final int            endTick;
        private       boolean        cleaned;

        public CEffectBigBadVoodoo(
                final CUnit        caster,
                final List<CUnit>  affectedUnits,
                final StateModBuff stunBuff,
                final StateModBuff disableSpellBuff,
                final int          endTick) {
            this.caster          = caster;
            this.affectedUnits   = affectedUnits;
            this.stunBuff        = stunBuff;
            this.disableSpellBuff = disableSpellBuff;
            this.endTick         = endTick;
            this.cleaned         = false;
        }

        @Override
        public boolean update(final CSimulation game) {
            if (this.cleaned) {
                return true;
            }

            // Fin normale (durée écoulée) ou interruption par la mort du caster
            final boolean expired     = game.getGameTurnTick() >= this.endTick;
            final boolean casterDead  = this.caster.isDead();

            if (!expired && !casterDead) {
                return false;
            }

            removeEffects(game);
            return true;
        }

        private void removeEffects(final CSimulation game) {
            this.cleaned = true;

            // Retrait de l'invulnérabilité sur toutes les unités affectées
            for (final CUnit ally : this.affectedUnits) {
                if (!ally.isDead()) {
                    ally.setInvulnerable(false);
                }
            }

            // Retrait du stun et du disable-spells sur le caster
            if (!this.caster.isDead()) {
                this.caster.removeStateModBuff(this.stunBuff);
                this.caster.removeStateModBuff(this.disableSpellBuff);
                this.caster.computeUnitState(game, StateModBuffType.STUN);
                this.caster.computeUnitState(game, StateModBuffType.DISABLE_SPELLS);
            }
        }
    }
}