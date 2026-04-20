package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

import java.util.ArrayList;
import java.util.List;

public class CAbilityMirrorImage extends CAbilityNoTargetSpellBase {

    private int    numberOfImages;
    private float  damagePercent;
    private float  duration;
    private War3ID buffId;

    private final List<CUnit> activeImages = new ArrayList<>();

    public CAbilityMirrorImage(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.numberOfImages = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
        this.damagePercent  = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0) / 100f;
        this.duration       = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
        this.buffId         = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.mirrorimage;
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        this.activeImages.removeIf(CUnit::isDead);

        final float facing = caster.getFacing();
        final float offset = 120f;

        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);

        for (int i = 0; i < this.numberOfImages; i++) {
            final float angle = (float) (i * (2.0 * Math.PI / this.numberOfImages));
            final float x = caster.getX() + (float) Math.cos(angle) * offset;
            final float y = caster.getY() + (float) Math.sin(angle) * offset;

            final CUnit image = simulation.createUnitSimple(
                    caster.getTypeId(),
                    caster.getPlayerIndex(),
                    x, y,
                    facing
            );

            image.addClassification(CUnitClassification.SUMMONED);
            image.setLife(simulation, caster.getLife());
            image.setMana(caster.getMana());

            // Teinte bleue semi-transparente (même rendu que les unités éthérées)
            simulation.changeUnitVertexColor(image, RenderUnit.ETHEREAL);

            // ------------------------------------------------------------------
            // Disparition sans animation ni explosion :
            //
            // Cas 1 – HP tombe à 0 (image tuée par des dégâts)
            //   setExplodesOnDeath(true)   → dans CUnit.kill() : setHidden + removeUnit
            //   explodesOnDeathBuffId = War3ID.NONE → createDeathExplodeEffect ne trouve
            //   aucun art associé à NONE → aucun visuel, le clone disparaît proprement.
            //
            // Cas 2 – Buff expire (durée écoulée)
            //   CBuffMirrorImageTimed.onBuffRemove → setHidden + removeUnit directement,
            //   sans passer par kill() qui jouerait une animation de mort.
            // ------------------------------------------------------------------
            image.setExplodesOnDeath(true);
            image.setExplodesOnDeathBuffId(War3ID.NONE);

            image.add(simulation, new CBuffMirrorImageTimed(
                    simulation.getHandleIdAllocator().createId(),
                    this.buffId,
                    this.duration
            ));

            simulation.createTemporarySpellEffectOnUnit(image, getAlias(), CEffectType.SPECIAL);

            this.activeImages.add(image);
        }

        return false;
    }

    // =========================================================================
    // Buff interne : gère l'expiration propre du clone
    // =========================================================================
    private static final class CBuffMirrorImageTimed extends CBuffTimed {

        public CBuffMirrorImageTimed(final int handleId, final War3ID alias, final float duration) {
            super(handleId, alias, alias, duration);
        }

        @Override
        protected void onBuffAdd(final CSimulation game, final CUnit unit) {
            // Rien à faire ici : explode + War3ID.NONE déjà posés dans doEffect
        }

        @Override
        protected void onBuffRemove(final CSimulation game, final CUnit unit) {
            // Expiration de la durée : cacher et retirer directement,
            // sans appeler unit.kill() qui jouerait une animation de mort.
            unit.setHidden(true);
            game.removeUnit(unit);
        }

        @Override
        public boolean isTimedLifeBar() {
            return false;
        }
    }
}