package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

public class CBuffHex extends CBuffTimed {

    private final CUnitType polymorphUnitType;

    public CBuffHex(final int handleId, final War3ID alias, final float duration,
                    final CUnitType polymorphUnitType) {
        super(handleId, alias, alias, duration);
        this.polymorphUnitType = polymorphUnitType;
    }

    @Override
    protected void onBuffAdd(final CSimulation game, final CUnit unit) {
        unit.addClassification(CUnitClassification.POLYMORPHED);

        // Enregistrement des state mods dans la liste
        unit.addStateModBuff(new StateModBuff(StateModBuffType.DISABLE_ATTACK, getHandleId()));
        unit.addStateModBuff(new StateModBuff(StateModBuffType.DISABLE_AUTO_ATTACK, getHandleId()));
        unit.addStateModBuff(new StateModBuff(StateModBuffType.DISABLE_SPELLS, getHandleId()));
        unit.addStateModBuff(new StateModBuff(StateModBuffType.SNARED, getHandleId()));

        // Application effective des effets — addStateModBuff seul ne suffit pas,
        // computeUnitState est indispensable pour que les états soient réellement actifs.
        unit.computeUnitState(game, StateModBuffType.DISABLE_ATTACK);
        unit.computeUnitState(game, StateModBuffType.DISABLE_AUTO_ATTACK);
        unit.computeUnitState(game, StateModBuffType.DISABLE_SPELLS);
        unit.computeUnitState(game, StateModBuffType.SNARED);

        // Transformation visuelle — passe le CSimulation pour que applyPolymorph
        // puisse appeler game.unitUpdatedType et swaper le modèle côté rendu.
        if (this.polymorphUnitType != null) {
            unit.applyPolymorph(game, this.polymorphUnitType);
        }
    }

    @Override
    protected void onBuffRemove(final CSimulation game, final CUnit unit) {
        unit.removeClassification(CUnitClassification.POLYMORPHED);

        // Retrait des state mods — même identifiant (getHandleId) qu'à l'ajout
        unit.removeStateModBuff(new StateModBuff(StateModBuffType.DISABLE_ATTACK, getHandleId()));
        unit.removeStateModBuff(new StateModBuff(StateModBuffType.DISABLE_AUTO_ATTACK, getHandleId()));
        unit.removeStateModBuff(new StateModBuff(StateModBuffType.DISABLE_SPELLS, getHandleId()));
        unit.removeStateModBuff(new StateModBuff(StateModBuffType.SNARED, getHandleId()));

        // Recalcul des états après retrait
        unit.computeUnitState(game, StateModBuffType.DISABLE_ATTACK);
        unit.computeUnitState(game, StateModBuffType.DISABLE_AUTO_ATTACK);
        unit.computeUnitState(game, StateModBuffType.DISABLE_SPELLS);
        unit.computeUnitState(game, StateModBuffType.SNARED);

        if (this.polymorphUnitType != null) {
            unit.removePolymorph(game);
        }
    }

    @Override
    public boolean isTimedLifeBar() {
        return false;
    }
}