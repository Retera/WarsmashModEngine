package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class ABActionDamageTarget implements ABAction {

	private ABUnitCallback source;
	private ABUnitCallback target;
	private ABBooleanCallback isAttack;
	private ABBooleanCallback isRanged;
	private ABAttackTypeCallback attackType;
	private ABDamageTypeCallback damageType;
	private ABFloatCallback damage;
	
	private ABBooleanCallback ignoreLTEZero;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		boolean isItAttack = false;
		boolean isItRanged = true;
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;
		
		float theDamage = damage.callback(game, caster, localStore, castId);

		if (isAttack != null) {
			isItAttack = isAttack.callback(game, caster, localStore, castId);
		}
		if (isRanged != null) {
			isItRanged = isRanged.callback(game, caster, localStore, castId);
		}
		if (attackType != null) {
			theAttackType = attackType.callback(game, caster, localStore, castId);
		}
		if (damageType != null) {
			theDamageType = damageType.callback(game, caster, localStore, castId);
		}
		if (ignoreLTEZero == null || !ignoreLTEZero.callback(game, caster, localStore, castId) || theDamage > 0) {
			target.callback(game, caster, localStore, castId).damage(game, source.callback(game, caster, localStore, castId), isItAttack,
					isItRanged, theAttackType, theDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
					damage.callback(game, caster, localStore, castId));
		}
	}

}
