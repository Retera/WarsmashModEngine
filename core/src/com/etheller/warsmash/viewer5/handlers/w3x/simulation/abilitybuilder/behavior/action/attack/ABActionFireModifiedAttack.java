
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.attack;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.attack.ABAttackModifierCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABAttackModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionFireModifiedAttack implements ABAction {

	private ABUnitCallback unit;
	private ABUnitCallback target;
	private ABAttackModifierCallback modifier;

	private ABBooleanCallback showMissOnFailure;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(game, caster, localStore, castId);
		}
		CUnit theTarget = target.callback(game, caster, localStore, castId);

		for (final CUnitAttack attack : theUnit.getCurrentAttacks()) {
			if (theUnit.canReach(theTarget, attack.getRange() + attack.getRangeMotionBuffer())
					&& theTarget.canBeTargetedBy(game, theUnit, attack.getTargetsAllowed())
					&& (theUnit.getUnitType().getMinimumAttackRange() == 0
							|| theUnit.distance(theTarget) >= theUnit.getUnitType().getMinimumAttackRange())) {
				if (!theTarget.isImmuneToDamage(game, null, attack.getAttackType(),
						attack.getWeaponType().getDamageType())) {
					ABAttackModifier mod = null;
					if (modifier != null) {
						mod = modifier.callback(game, caster, localStore, castId);
					}
					CUnitAttackSettings settings = attack.initialSettings();
					if (mod != null) {
						if (mod.checkPreLaunchApplication(game, theUnit, theTarget, attack)) {
							mod.applyPreLaunchModification(game, theUnit, theTarget, attack, settings, null);
						}
						if (mod.checkApplication(game, theUnit, theTarget, attack)) {
							mod.applyModification(game, theUnit, theTarget, attack, settings, null);
						}
					}
					if (settings.getPreDamageListeners() == null) {
						settings.setEmptyPreDamageListeners();
					}

					attack.launch(game, theUnit, theTarget, attack.roll(game.getSeededRandom()),
							CBehaviorAttackListener.DO_NOTHING);
					return;
				}
			}
		}
		boolean show = false;
		if (showMissOnFailure != null) {
			show = showMissOnFailure.callback(game, caster, localStore, castId);
		}
		if (show) {
			game.spawnTextTag(theUnit, theUnit.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss");
		}
	}
}