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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class ABActionDamageTarget implements ABAction {

	private ABUnitCallback source;
	private ABUnitCallback target;
	private ABBooleanCallback isAttack;
	private ABBooleanCallback isRanged;
	private ABAttackTypeCallback attackType;
	private ABDamageTypeCallback damageType;
	private ABFloatCallback damage;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		target.callback(game, caster, localStore).damage(game, source.callback(game, caster, localStore),
				isAttack.callback(game, caster, localStore), isRanged.callback(game, caster, localStore),
				attackType.callback(game, caster, localStore), damageType.callback(game, caster, localStore),
				CWeaponSoundTypeJass.WHOKNOWS.name(), damage.callback(game, caster, localStore));
	}

}
