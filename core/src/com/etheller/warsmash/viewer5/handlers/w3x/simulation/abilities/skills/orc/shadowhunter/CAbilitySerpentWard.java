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

/**
 * Capacité "Serpent Ward" du Shadow Hunter (Orcs).
 *
 * <p>Le Shadow Hunter pose un serpent gardien à un point cible.
 * Contrairement aux loups (FeralSpirit) et à l'élémentaire d'eau
 * (SummonWaterElemental) qui sont des sorts sans cible, le Serpent Ward
 * cible un point précis sur la carte.</p>
 *
 * <p>Contraintes :</p>
 * <ul>
 *   <li>Cible un point (hérite de {@link CAbilityPointTargetSpellBase}).</li>
 *   <li>Le nombre de wards actifs simultanément est limité par niveau
 *       (DataA dans le SLK).</li>
 *   <li>Chaque nouveau lancer supprime les anciens wards du même lanceur
 *       si la limite est atteinte (comportement WC3 : le plus ancien est
 *       remplacé).</li>
 *   <li>La durée de vie du ward est gérée via {@link CBuffTimedLife}.</li>
 * </ul>
 */
public class CAbilitySerpentWard extends CAbilityPointTargetSpellBase {

    private War3ID summonUnitId;
    private int maxWardCount;
    private War3ID buffId;
    private float duration;



    private final List<CUnit> lastSummonUnits = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Constructeur
    // -----------------------------------------------------------------------

    public CAbilitySerpentWard(final int handleId, final War3ID alias) {
        super(handleId, alias);
        System.out.println("CAbilitySerpentWard created: " + alias);
    }

    // -----------------------------------------------------------------------
    // Surcharges obligatoires
    // -----------------------------------------------------------------------

    /**
     * Remplit les données depuis les fichiers SLK pour le niveau spécifié.
     * Appelé par le moteur lors de l'initialisation ou du level-up.
     *
     * <p>Champs lus :</p>
     * <ul>
     *   <li>{@link AbilityFields#UNIT_ID} + level : rawcode de l'unité ward.</li>
     *   <li>{@link AbilityFields#DATA_A} + level : nombre max de wards.</li>
     *   <li>buffId via {@link AbstractCAbilityTypeDefinition#getBuffId}.</li>
     * </ul>
     *
     * @param worldEditorAbility objet contenant les données SLK de la capacité
     * @param level              niveau courant de la capacité (commence à 1)
     */
    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        final String unitTypeRaw =
                worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0);
        this.summonUnitId = (unitTypeRaw.length() == 4)
                ? War3ID.fromString(unitTypeRaw)
                : War3ID.NONE;
        this.maxWardCount =
                worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
        this.buffId =
                AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
        this.duration  = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);

    }


    @Override
    public int getBaseOrderId() {
        return OrderIds.ward;
    }

    /**
     * Effectue l'effet du sort : pose un Serpent Ward au point cible.
     *
     * <p>Étapes :</p>
     * <ol>
     *   <li>Récupération du point cible.</li>
     *   <li>Si le nombre max de wards est atteint, tue le plus ancien.</li>
     *   <li>Crée l'unité ward au point cible.</li>
     *   <li>Marque l'unité comme invoquée ({@link CUnitClassification#SUMMONED}).</li>
     *   <li>Ajoute un buff de durée de vie ({@link CBuffTimedLife}).</li>
     *   <li>Crée l'effet visuel d'apparition.</li>
     *   <li>Enregistre le ward dans la liste des wards actifs.</li>
     * </ol>
     *
     * @param simulation la simulation de jeu
     * @param unit       le Shadow Hunter qui lance le sort
     * @param target     le point cible sur la carte
     * @return {@code false} (le sort n'est pas un sort à canal continu)
     */
    @Override
    public boolean doEffect(
            final CSimulation simulation,
            final CUnit unit,
            final AbilityTarget target) {

        // -------------------------------------------------------------------
        // Récupération des coordonnées du point cible
        // -------------------------------------------------------------------
        final AbilityPointTarget pointTarget = (AbilityPointTarget) target;
        final float x = pointTarget.getX();
        final float y = pointTarget.getY();
        final float facing = unit.getFacing();

        // -------------------------------------------------------------------
        // Gestion de la limite de wards actifs
        // Nettoyer les wards morts de la liste, puis tuer le plus ancien
        // si on atteint la limite (comportement WC3 standard)
        // -------------------------------------------------------------------
        this.lastSummonUnits.removeIf(CUnit::isDead);

        if (this.lastSummonUnits.size() >= getMaxWardCount()+2) {
            // Tuer le ward le plus ancien (indice 0 = premier ajouté)
            final CUnit oldestWard = this.lastSummonUnits.remove(0);
            if (!oldestWard.isDead()) {
                oldestWard.kill(simulation);
            }
        }

        // -------------------------------------------------------------------
        // Création du ward au point cible
        // -------------------------------------------------------------------
        final CUnit summonedUnit = simulation.createUnitSimple(
                this.summonUnitId,
                unit.getPlayerIndex(),
                x,
                y,
                facing
        );

        // Marquer comme unité invoquée (affecte le comportement en jeu)
        summonedUnit.addClassification(CUnitClassification.SUMMONED);

        // Ajouter le buff de durée de vie (true = expiration silencieuse)
        summonedUnit.add(
                simulation,
                new CBuffTimedLife(
                        simulation.getHandleIdAllocator().createId(),
                        this.buffId,
                        this.duration,
                        true
                )
        );

        // Effet visuel d'apparition sur le ward
        simulation.createTemporarySpellEffectOnUnit(
                summonedUnit,
                getAlias(),
                CEffectType.SPECIAL
        );

        // Enregistrer le ward dans la liste des wards actifs
        this.lastSummonUnits.add(summonedUnit);

        return false;
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /**
     * Retourne le rawcode de l'unité Serpent Ward invoquée.
     *
     * @return rawcode de l'unité
     */
    public War3ID getSummonUnitId() {
        return this.summonUnitId;
    }

    /**
     * Retourne le nombre maximum de wards simultanés au niveau actuel.
     *
     * @return nombre maximum de wards
     */
    public int getMaxWardCount() {
        return this.maxWardCount;
    }

    /**
     * Retourne l'identifiant du buff de durée de vie.
     *
     * @return identifiant du buff
     */
    public War3ID getBuffId() {
        return this.buffId;
    }

    /**
     * Retourne la liste non modifiable des wards actifs.
     *
     * @return liste des wards actifs
     */
    public List<CUnit> getLastSummonUnits() {
        return java.util.Collections.unmodifiableList(this.lastSummonUnits);
    }

    // -----------------------------------------------------------------------
    // Setters (pour le World Editor / outils)
    // -----------------------------------------------------------------------

    /**
     * Définit le rawcode de l'unité à invoquer.
     *
     * @param summonUnitId rawcode de l'unité
     */
    public void setSummonUnitId(final War3ID summonUnitId) {
        this.summonUnitId = summonUnitId;
    }

    /**
     * Définit le nombre maximum de wards simultanés.
     *
     * @param maxWardCount nombre maximum
     */
    public void setMaxWardCount(final int maxWardCount) {
        this.maxWardCount = maxWardCount;
    }

    /**
     * Définit l'identifiant du buff de durée de vie.
     *
     * @param buffId identifiant du buff
     */
    public void setBuffId(final War3ID buffId) {
        this.buffId = buffId;
    }
}