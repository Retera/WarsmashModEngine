package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.shadowhunter;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class CAbilitySerpentWard extends CAbilityPointTargetSpellBase {

    private War3ID summonUnitId;

    // BUG CORRIGÉ : DATA_A retournait 1 (ce sont les dégâts/seconde du ward).
    // La limite de wards simultanés est stockée dans DATA_B en WC3 standard.
    // Niveau 1 = 3, Niveau 2 = 6, Niveau 3 = 10.
    private int    maxWardCount;

    private War3ID buffId;
    private float  duration;

    // Liste des wards actifs posés par CE lanceur
    private final List<CUnit> activeWards = new ArrayList<>();

    public CAbilitySerpentWard(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    // -------------------------------------------------------------------------
    // Données SLK — appelé à l'init ET à chaque level-up
    // -------------------------------------------------------------------------

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        final String unitTypeRaw = worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0);
        this.summonUnitId = (unitTypeRaw.length() == 4)
                ? War3ID.fromString(unitTypeRaw)
                : War3ID.NONE;

        // DATA_B = nombre maximum de wards actifs simultanément (3 / 6 / 10)
        this.maxWardCount = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_B + level, 0);

        this.buffId   = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
        this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.ward;
    }

    // -------------------------------------------------------------------------
    // Effet du sort
    // -------------------------------------------------------------------------

    @Override
    public boolean doEffect(
            final CSimulation simulation,
            final CUnit caster,
            final AbilityTarget target) {

        final AbilityPointTarget pointTarget = (AbilityPointTarget) target;
        final float x      = pointTarget.getX();
        final float y      = pointTarget.getY();
        final float facing = caster.getFacing();

        // --------------------------------------------------------------------
        // 1. Retirer de la liste les wards déjà morts naturellement
        // --------------------------------------------------------------------
        this.activeWards.removeIf(CUnit::isDead);

        // --------------------------------------------------------------------
        // 2. BUG CORRIGÉ : seuil correct pour la limite de wards simultanés.
        //
        //    Ancien code : size() >= maxWardCount + 2  ← trop permissif / faux
        //    Nouveau code : size() >= maxWardCount     ← on tue le plus ancien
        //                                                 si la limite est atteinte
        //
        //    Flux correct :
        //      - avant d'ajouter le nouveau ward, la liste a au plus
        //        (maxWardCount - 1) éléments après le retrait du plus ancien
        //      - le nouveau ward est ajouté → la liste reste à maxWardCount
        // --------------------------------------------------------------------
        if (this.activeWards.size() >= this.maxWardCount) {
            final CUnit oldest = this.activeWards.remove(0);
            if (!oldest.isDead()) {
                oldest.kill(simulation);
            }
        }

        // --------------------------------------------------------------------
        // 3. Création du ward au point cible
        // --------------------------------------------------------------------
        final CUnit ward = simulation.createUnitSimple(
                this.summonUnitId,
                caster.getPlayerIndex(),
                x, y,
                facing
        );

        ward.addClassification(CUnitClassification.SUMMONED);

        ward.add(simulation, new CBuffTimedLife(
                simulation.getHandleIdAllocator().createId(),
                this.buffId,
                this.duration,
                true
        ));

        simulation.createTemporarySpellEffectOnUnit(ward, getAlias(), CEffectType.SPECIAL);

        // --------------------------------------------------------------------
        // 4. Enregistrement du ward dans la liste des wards actifs
        // --------------------------------------------------------------------
        this.activeWards.add(ward);

        return false;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public War3ID getSummonUnitId()          { return this.summonUnitId;  }
    public int    getMaxWardCount()          { return this.maxWardCount;  }
    public War3ID getBuffId()                { return this.buffId;        }

    public List<CUnit> getActiveWards() {
        return java.util.Collections.unmodifiableList(this.activeWards);
    }

    public void setSummonUnitId(final War3ID summonUnitId) { this.summonUnitId = summonUnitId; }
    public void setMaxWardCount(final int maxWardCount)    { this.maxWardCount = maxWardCount; }
    public void setBuffId(final War3ID buffId)             { this.buffId       = buffId;       }
}