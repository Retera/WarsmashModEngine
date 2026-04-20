package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.taurenchieftain;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffectPriority;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementStacking;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityReincarnation extends CAbilityNoTargetSpellBase {

    // Durée du délai avant la résurrection (en secondes)
    private float reviveDelay;
    // Durée de rechargement (cooldown) avant que la passive puisse se déclencher à nouveau
    private float cooldownTime;

    // Référence au listener de mort pour retrait propre dans onRemove
    private ReincarnationDeathEffect deathEffect;

    public CAbilityReincarnation(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    // -------------------------------------------------------------------------
    // Données SLK — appelé à l'init ET à chaque level-up
    // -------------------------------------------------------------------------

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        // DURATION = délai avant résurrection (ex : 3 secondes en WC3)
        this.reviveDelay  = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
        // COOLDOWN = temps de rechargement avant nouveau déclenchement possible
        this.cooldownTime = worldEditorAbility.getFieldAsFloat(AbilityFields.COOLDOWN + level, 0);
    }

    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
        // Passive : pas de CBehaviorNoTargetSpellBase (super.onAdd non appelé)
        this.deathEffect = new ReincarnationDeathEffect();
        unit.addDeathReplacementEffect(CUnitDeathReplacementEffectPriority.GENERALONDEATHACTIONS, this.deathEffect);
    }

    @Override
    public void onRemove(final CSimulation game, final CUnit unit) {
        if (this.deathEffect != null) {
            unit.removeDeathReplacementEffect(CUnitDeathReplacementEffectPriority.GENERALONDEATHACTIONS, this.deathEffect);
            this.deathEffect = null;
        }
    }

    // -------------------------------------------------------------------------
    // Passive : aucune activation manuelle possible
    // -------------------------------------------------------------------------

    @Override
    public int getBaseOrderId() {
        return OrderIds.reincarnation;
    }

    @Override
    protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit,
                                               final int orderId, final AbilityTargetCheckReceiver<Void> receiver) {
        receiver.orderIdNotAccepted();
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit unit,
                            final AbilityTarget target) {
        return false;
    }

    // =========================================================================
    // Listener de remplacement de mort — déclenché par CUnit.kill()
    // =========================================================================

    private final class ReincarnationDeathEffect implements CUnitDeathReplacementEffect {

        @Override
        public CUnitDeathReplacementStacking onDeath(
                final CSimulation simulation,
                final CUnit unit,
                final CUnit source,
                final CUnitDeathReplacementResult result) {

            // ----------------------------------------------------------------
            // Vérification du cooldown : si la passive est encore en
            // rechargement, on laisse la mort se produire normalement.
            // ----------------------------------------------------------------
            if (unit.getCooldownRemainingTicks(simulation, getAlias()) > 0) {
                return new CUnitDeathReplacementStacking();
            }

            // ----------------------------------------------------------------
            // Prévention de la mort : result.setReviving(true) fait retourner
            // kill() immédiatement, avant toute suppression de l'unité.
            // ----------------------------------------------------------------
            result.setReviving(true);

            // ----------------------------------------------------------------
            // Remettre 1 HP pour que isDead() retourne false et éviter toute
            // re-entrée dans kill() lors des prochains ticks.
            // heal() appelle setLife() qui gère la notification UI.
            // ----------------------------------------------------------------
            unit.heal(simulation, 1f);

            // ----------------------------------------------------------------
            // Rendre le Tauren invulnérable pendant le délai de résurrection
            // ----------------------------------------------------------------
            unit.setInvulnerable(true);

            // ----------------------------------------------------------------
            // Mettre la passive en cooldown pour éviter un déclenchement
            // immédiat lors de la prochaine mort (ex : pendant la résurrection)
            // ----------------------------------------------------------------
            unit.beginCooldown(simulation, getAlias(), cooldownTime);

            // ----------------------------------------------------------------
            // Effet visuel de début de résurrection sur le Tauren
            // ----------------------------------------------------------------
            simulation.createTemporarySpellEffectOnUnit(unit, getAlias(), CEffectType.CASTER);

            // ----------------------------------------------------------------
            // Enregistrement de l'effet temporisé qui gère le délai puis
            // la résurrection complète (HP/mana pleins + fin d'invulnérabilité)
            // ----------------------------------------------------------------
            final int reviveTick = simulation.getGameTurnTick()
                    + (int) StrictMath.ceil(reviveDelay / WarsmashConstants.SIMULATION_STEP_TIME);

            simulation.registerEffect(new CEffectReincarnation(unit, reviveTick, getAlias()));

            // Retourner un stacking par défaut (ne bloque pas les autres effets)
            return new CUnitDeathReplacementStacking();
        }
    }

    // =========================================================================
    // Effet interne : gère le délai puis la résurrection
    // =========================================================================

    private static final class CEffectReincarnation implements CEffect {

        private final CUnit   unit;
        private final int     reviveTick;
        private final War3ID  alias;
        private       boolean done;

        public CEffectReincarnation(final CUnit unit, final int reviveTick, final War3ID alias) {
            this.unit      = unit;
            this.reviveTick = reviveTick;
            this.alias     = alias;
            this.done      = false;
        }

        @Override
        public boolean update(final CSimulation game) {
            if (this.done) {
                return true;
            }

            // Si l'unité a quand même été supprimée entre-temps (cas extrême),
            // on annule proprement sans rien faire.
            if (this.unit.isHidden()) {
                this.done = true;
                return true;
            }

            if (game.getGameTurnTick() < this.reviveTick) {
                return false;
            }

            this.done = true;

            // ----------------------------------------------------------------
            // Résurrection : HP et mana au maximum
            // ----------------------------------------------------------------
            this.unit.setLife(game, this.unit.getMaximumLife());
            this.unit.setMana(this.unit.getMaximumMana());

            // ----------------------------------------------------------------
            // Fin de l'invulnérabilité
            // ----------------------------------------------------------------
            this.unit.setInvulnerable(false);

            // ----------------------------------------------------------------
            // Effet visuel de fin de résurrection
            // ----------------------------------------------------------------
            game.createTemporarySpellEffectOnUnit(this.unit, this.alias, CEffectType.SPECIAL);

            return true;
        }
    }
}