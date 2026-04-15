package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.shadowhunter;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffHex;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityHex extends CAbilityTargetSpellBase {

    private War3ID buffId;
    private War3ID polymorphUnitId;


    public CAbilityHex(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
        // Si tu veux un polymorph unit-type configurable dans l’éditeur (comme pour Serpent Ward),
        final String unitRaw = worldEditorAbility.getFieldAsString(AbilityFields.UNIT_ID + level, 0);
        this.polymorphUnitId = unitRaw.length() == 4 ? War3ID.fromString(unitRaw) : War3ID.fromString("nshe");
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.hex;
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
        final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
        if (targetUnit != null) {
            System.out.println("HEX lancé sur : " + targetUnit.getUnitType().getName()); // ← Debug

            final float duration = getDurationForTarget(targetUnit);

            final CUnitType sheepType = simulation.getUnitData().getUnitType(War3ID.fromString("nshe"));

            if (sheepType == null) {
                System.err.println("ERREUR : nshe non trouvé dans unit data !");
                return false;
            }

            // On force le buff
            targetUnit.add(simulation, new CBuffHex(
                    simulation.getHandleIdAllocator().createId(),
                    this.buffId,
                    duration,
                    sheepType
            ));

            simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
            System.out.println("CBuffHex ajouté avec succès !");
        }
        return false;
    }
}