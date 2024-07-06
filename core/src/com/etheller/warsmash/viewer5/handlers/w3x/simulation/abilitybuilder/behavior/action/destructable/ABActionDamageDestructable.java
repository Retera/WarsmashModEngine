package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class ABActionDamageDestructable implements ABSingleAction {

	private ABUnitCallback source;
	private ABDestructableCallback target;
	private ABBooleanCallback isAttack;
	private ABBooleanCallback isRanged;
	private ABAttackTypeCallback attackType;
	private ABDamageTypeCallback damageType;
	private ABFloatCallback damage;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		boolean isItAttack = false;
		boolean isItRanged = true;
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;

		if (this.isAttack != null) {
			isItAttack = this.isAttack.callback(game, caster, localStore, castId);
		}
		if (this.isRanged != null) {
			isItRanged = this.isRanged.callback(game, caster, localStore, castId);
		}
		if (this.attackType != null) {
			theAttackType = this.attackType.callback(game, caster, localStore, castId);
		}
		if (this.damageType != null) {
			theDamageType = this.damageType.callback(game, caster, localStore, castId);
		}

		this.target.callback(game, caster, localStore, castId).damage(game,
				this.source.callback(game, caster, localStore, castId), isItAttack, isItRanged, theAttackType,
				theDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
				this.damage.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		// TODO Auto-generated method stub
//		native UnitDamageTarget takes unit whichUnit, widget target, real amount, boolean attack, boolean ranged, attacktype attackType, damagetype damageType, weapontype weaponType returns boolean

		String attackExpression = "false";
		if (this.isAttack != null) {
			attackExpression = this.isAttack.generateJassEquivalent(jassTextGenerator);
		}
		String rangedExpression = "true";
		if (this.isRanged != null) {
			rangedExpression = this.isRanged.generateJassEquivalent(jassTextGenerator);
		}
		String attackTypeExpression = "ATTACK_TYPE_SPELLS";
		String damageTypeExpression = "DAMAGE_TYPE_MAGIC";
		if (this.attackType != null) {
			attackTypeExpression = this.attackType.generateJassEquivalent(jassTextGenerator);
		}
		if (this.damageType != null) {
			damageTypeExpression = this.damageType.generateJassEquivalent(jassTextGenerator);
		}

		return "UnitDamageTarget(" + this.source.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.damage.generateJassEquivalent(jassTextGenerator) + ", " + attackExpression + ", "
				+ rangedExpression + ", " + attackTypeExpression + ", " + damageTypeExpression
				+ ", WEAPON_TYPE_WHOKNOWS)";
	}

}
