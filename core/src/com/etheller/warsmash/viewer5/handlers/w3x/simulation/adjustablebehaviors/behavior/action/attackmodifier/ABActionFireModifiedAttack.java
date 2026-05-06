
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.attack.ABAttackModifierCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABAttackModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionFireModifiedAttack implements ABAction {

	private ABUnitCallback unit;
	private ABUnitCallback target;
	private ABAttackModifierCallback modifier;

	private ABBooleanCallback showMissOnFailure;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(caster, localStore, castId);
		}
		CUnit theTarget = target.callback(caster, localStore, castId);

		for (final CUnitAttack attack : theUnit.getCurrentAttacks()) {
			if (theUnit.canReach(theTarget, attack.getRange() + attack.getRangeMotionBuffer())
					&& theTarget.canBeTargetedBy(localStore.game, theUnit, attack.getTargetsAllowed())
					&& (theUnit.getUnitType().getMinimumAttackRange() == 0
							|| theUnit.distance(theTarget) >= theUnit.getUnitType().getMinimumAttackRange())) {
				if (!theTarget.isImmuneToDamage(localStore.game, null, attack.getAttackType(),
						attack.getWeaponType().getDamageType())) {
					ABAttackModifier mod = null;
					if (modifier != null) {
						mod = modifier.callback(caster, localStore, castId);
					}
					CUnitAttackSettings settings = attack.initialSettings();
					if (mod != null) {
						if (mod.checkPreLaunchApplication(localStore.game, theUnit, theTarget, attack)) {
							mod.applyPreLaunchModification(localStore.game, theUnit, theTarget, attack, settings, null);
						}
						if (mod.checkApplication(localStore.game, theUnit, theTarget, attack)) {
							mod.applyModification(localStore.game, theUnit, theTarget, attack, settings, null);
						}
					}
					if (settings.getPreDamageListeners() == null) {
						settings.setEmptyPreDamageListeners();
					}

					attack.launch(localStore.game, theUnit, theTarget, attack.roll(localStore.game.getSeededRandom()),
							CBehaviorAttackListener.DO_NOTHING);
					return;
				}
			}
		}
		boolean show = false;
		if (showMissOnFailure != null) {
			show = showMissOnFailure.callback(caster, localStore, castId);
		}
		if (show) {
			localStore.game.spawnTextTag(theUnit, theUnit.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss");
		}
	}
}