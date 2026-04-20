package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.taurenchieftain;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CAbilityEnduranceAura extends CAbilityNoTargetSpellBase {

    // DATA_A = bonus de vitesse de déplacement (ex : 0.10 pour +10 %)
    private float movementSpeedBonus;
    // DATA_B = bonus de vitesse d'attaque (ex : 0.10 pour +10 %)
    private float attackSpeedBonus;
    // Rayon de l'aura
    private float areaOfEffect;

    // Clé de non-empilement : basée sur l'alias (rawcode WC3 de la capacité).
    // Deux auras du même type provenant de héros différents ne s'empilent pas,
    // seul le bonus le plus élevé s'applique (comportement WC3 standard).
    private String stackingKey;

    // Unités actuellement sous l'effet de l'aura.
    // Valeur : tableau [buffVitesseDéplacement, buffVitesseAttaque] pour pouvoir
    // les retirer proprement quand une unité quitte la zone.
    private final Map<CUnit, NonStackingStatBuff[]> buffedUnits = new HashMap<>();

    public CAbilityEnduranceAura(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    // -------------------------------------------------------------------------
    // Données SLK — appelé à l'init ET à chaque level-up
    // -------------------------------------------------------------------------

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.movementSpeedBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        this.attackSpeedBonus   = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
        this.areaOfEffect       = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
    }

    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        // Passive : pas de CBehaviorNoTargetSpellBase (super.onAdd non appelé)
        // Initialisation de la clé de non-empilement
        this.stackingKey = getAlias().asStringValue();
    }

    @Override
    public void onRemove(final CSimulation game, final CUnit unit) {
        // Retrait propre de tous les buffs d'aura lors du retrait de la capacité
        removeAllBuffs(game);
    }

    @Override
    public void onDeath(final CSimulation game, final CUnit unit) {
        // Si le Tauren meurt, les buffs d'aura sont retirés immédiatement
        removeAllBuffs(game);
    }

    // -------------------------------------------------------------------------
    // Tick principal — appliqué chaque pas de simulation
    // -------------------------------------------------------------------------

    @Override
    public void onTick(final CSimulation game, final CUnit caster) {
        if (caster.isDead() || caster.isHidden()) {
            return;
        }

        // --------------------------------------------------------------------
        // 1. Collecte des alliés vivants actuellement dans la zone de l'aura
        // --------------------------------------------------------------------
        final Set<CUnit> currentInRange = new HashSet<>();
        game.getWorldCollision().enumUnitsInRange(
                caster.getX(), caster.getY(), this.areaOfEffect,
                enumUnit -> {
                    if (!enumUnit.isDead()
                            && enumUnit.isUnitAlly(game.getPlayer(caster.getPlayerIndex()))) {
                        currentInRange.add(enumUnit);
                    }
                    return false;
                });

        // --------------------------------------------------------------------
        // 2. Retrait du buff pour les unités qui ont quitté la zone ou qui
        //    sont mortes depuis le dernier tick
        // --------------------------------------------------------------------
        final Iterator<Map.Entry<CUnit, NonStackingStatBuff[]>> iter =
                this.buffedUnits.entrySet().iterator();

        while (iter.hasNext()) {
            final Map.Entry<CUnit, NonStackingStatBuff[]> entry = iter.next();
            final CUnit ally = entry.getKey();

            if (!currentInRange.contains(ally) || ally.isDead()) {
                removeBuffFromUnit(game, ally, entry.getValue());
                iter.remove();
            }
        }

        // --------------------------------------------------------------------
        // 3. Application du buff aux nouvelles unités entrées dans la zone
        // --------------------------------------------------------------------
        for (final CUnit ally : currentInRange) {
            if (!this.buffedUnits.containsKey(ally)) {
                final NonStackingStatBuff[] buffs = applyBuffToUnit(game, ally);
                this.buffedUnits.put(ally, buffs);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Applique les deux buffs d'aura (déplacement + attaque) à une unité
     * et retourne les références pour pouvoir les retirer plus tard.
     */
    private NonStackingStatBuff[] applyBuffToUnit(final CSimulation game, final CUnit ally) {
        // Bonus de vitesse de déplacement (pourcentage)
        final NonStackingStatBuff moveSpeedBuff = new NonStackingStatBuff(
                NonStackingStatBuffType.MVSPDPCT,
                this.stackingKey,
                this.movementSpeedBonus
        );

        // Bonus de vitesse d'attaque (valeur directe, même unité que ATKSPD dans CUnit)
        final NonStackingStatBuff attackSpeedBuff = new NonStackingStatBuff(
                NonStackingStatBuffType.ATKSPD,
                this.stackingKey,
                this.attackSpeedBonus
        );

        ally.addNonStackingStatBuff(moveSpeedBuff);
        ally.addNonStackingStatBuff(attackSpeedBuff);

        return new NonStackingStatBuff[] { moveSpeedBuff, attackSpeedBuff };
    }

    /**
     * Retire les deux buffs d'aura d'une unité.
     */
    private void removeBuffFromUnit(final CSimulation game, final CUnit ally,
                                    final NonStackingStatBuff[] buffs) {
        if (buffs == null) {
            return;
        }
        // removeNonStackingStatBuff appelle computeDerivedFields en interne
        if (!ally.isDead()) {
            ally.removeNonStackingStatBuff(buffs[0]); // vitesse de déplacement
            ally.removeNonStackingStatBuff(buffs[1]); // vitesse d'attaque
        }
    }

    /**
     * Retire tous les buffs d'aura (mort du caster ou retrait de la capacité).
     */
    private void removeAllBuffs(final CSimulation game) {
        for (final Map.Entry<CUnit, NonStackingStatBuff[]> entry : this.buffedUnits.entrySet()) {
            removeBuffFromUnit(game, entry.getKey(), entry.getValue());
        }
        this.buffedUnits.clear();
    }

    // -------------------------------------------------------------------------
    // Passive : aucune activation manuelle possible
    // -------------------------------------------------------------------------

    @Override
    public int getBaseOrderId() {
        return OrderIds.endurance;
    }

    /**
     * Passive : rejeter tous les ordres de cast manuel.
     */
    @Override
    protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit,
                                               final int orderId, final AbilityTargetCheckReceiver<Void> receiver) {
        receiver.orderIdNotAccepted();
    }

    /**
     * doEffect n'est jamais appelé (la passive n'est pas castable).
     */
    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit unit,
                            final AbilityTarget target) {
        return false;
    }
}