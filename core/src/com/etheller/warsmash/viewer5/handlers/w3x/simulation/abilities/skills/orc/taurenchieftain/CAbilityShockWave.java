package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.taurenchieftain;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

import java.util.ArrayList;
import java.util.List;

public class CAbilityShockWave extends CAbilityPointTargetSpellBase {

    private float damage;
    private float distance;
    private float areaOfEffect;       // largeur de la vague
    private float stunDuration;
    private War3ID buffId;

    public CAbilityShockWave(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
        this.distance = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
        this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
        this.stunDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.shockwave;
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        final AbilityPointTarget pointTarget = (AbilityPointTarget) target;

        final float startX = caster.getX();
        final float startY = caster.getY();
        final float endX = pointTarget.getX();
        final float endY = pointTarget.getY();

        // Direction et longueur
        final float dx = endX - startX;
        final float dy = endY - startY;
        final float length = (float) Math.hypot(dx, dy);

        if (length < 10f) return false; // trop court

        final float dirX = dx / length;
        final float dirY = dy / length;

        // Effet visuel sur le caster
        simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);

        // Liste des unités touchées
        final List<CUnit> hitUnits = new ArrayList<>();

        // On cherche dans un rayon un peu plus grand que la distance
        simulation.getWorldCollision().enumUnitsInRange(startX, startY, distance + 100, enumUnit -> {
            if (enumUnit.isDead()
                    || enumUnit.isUnitAlly(simulation.getPlayer(caster.getPlayerIndex()))
                    || !enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
                return false;
            }

            // Vérifie si l'unité est proche de la ligne de la Shockwave
            if (isPointNearLine(enumUnit.getX(), enumUnit.getY(), startX, startY, dirX, dirY, length, areaOfEffect)) {
                hitUnits.add(enumUnit);
            }
            return false;
        });

        // Appliquer l'effet sur chaque unité touchée
        for (CUnit victim : hitUnits) {
            victim.damage(simulation, caster, false, true, CAttackType.SPELLS,
                    CDamageType.UNIVERSAL, CWeaponSoundTypeJass.WHOKNOWS.name(), damage);

            victim.add(simulation, new CBuffStun(
                    simulation.getHandleIdAllocator().createId(),
                    this.buffId,
                    stunDuration
            ));
        }

        return false;
    }

    /**
     * Vérifie si un point est proche d'une ligne segmentée
     */
    private boolean isPointNearLine(float px, float py,
                                    float x1, float y1,
                                    float dirX, float dirY,
                                    float maxLength, float width) {

        float dx = px - x1;
        float dy = py - y1;

        // Projection sur la direction
        float proj = dx * dirX + dy * dirY;

        if (proj < 0 || proj > maxLength) {
            return false;
        }

        // Distance perpendiculaire à la ligne
        float perpDist = Math.abs(dx * dirY - dy * dirX);

        return perpDist <= width;
    }
}