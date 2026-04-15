package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimedLife;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

import java.util.ArrayList;
import java.util.List;

public class CAbilityMirrorImage extends CAbilityNoTargetSpellBase {

    private int numberOfImages;
    private float damagePercent;
    private float duration;
    private War3ID buffId;

    private final List<CUnit> activeImages = new ArrayList<>();

    public CAbilityMirrorImage(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.numberOfImages = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
        this.damagePercent = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0) / 100f;
        this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.mirrorimage;
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        activeImages.removeIf(CUnit::isDead);

        final float facing = caster.getFacing();
        final float offset = 120f;

        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);

        for (int i = 0; i < numberOfImages; i++) {
            final float angle = (float) (i * (2 * Math.PI / numberOfImages));
            final float x = caster.getX() + (float) Math.cos(angle) * offset;
            final float y = caster.getY() + (float) Math.sin(angle) * offset;

            final CUnit image = simulation.createUnitSimple(
                    caster.getTypeId(),
                    caster.getPlayerIndex(),
                    x, y,
                    facing
            );

            // Classifications importantes pour les illusions
            image.addClassification(CUnitClassification.SUMMONED);
            image.addClassification(CUnitClassification.ILLUSION);   // Si cette classification n'existe pas, on la supprimera

            // Copie état de base
            image.setLife(simulation, caster.getLife());
            image.setMana(caster.getMana());

            // === Effet visuel bleu transparent (Mirror Image style) ===
            // Méthode alternative plus sûre dans Warsmash
            simulation.unitUpdatedType(image, image.getTypeId()); // Force refresh du modèle
            if (image.getUnitAnimationListener() != null) {
                image.getUnitAnimationListener().playAnimation(false,
                        com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag.STAND,
                        com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils.EMPTY, 0, true);
            }

            // Durée de vie
            image.add(simulation, new CBuffTimedLife(
                    simulation.getHandleIdAllocator().createId(),
                    this.buffId,
                    this.duration,
                    false
            ));

            simulation.createTemporarySpellEffectOnUnit(image, getAlias(), CEffectType.SPECIAL);

            activeImages.add(image);
        }

        // Invulnérabilité temporaire du vrai Blademaster
        caster.setInvulnerable(true);
        final CTimer invulnTimer = new CTimer() {
            @Override
            public void onFire(final CSimulation game) {
                caster.setInvulnerable(false);
            }
        };
        simulation.registerTimer(invulnTimer);

        return false;
    }
}