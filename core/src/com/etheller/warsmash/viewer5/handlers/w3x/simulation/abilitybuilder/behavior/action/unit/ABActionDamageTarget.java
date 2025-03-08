package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CGenericDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class ABActionDamageTarget implements ABSingleAction {

	private ABUnitCallback source;
	private ABUnitCallback target;
	private ABBooleanCallback isAttack;
	private ABBooleanCallback isRanged;
	private ABAttackTypeCallback attackType;
	private ABDamageTypeCallback damageType;
	private ABFloatCallback damage;

	private ABBooleanCallback ignoreLTEZero;
	private ABBooleanCallback damageInvulnerable;
	private ABBooleanCallback explodeOnDeath;
	private ABBooleanCallback onlyDamageSummons;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;
		CDamageFlags flags = new CGenericDamageFlags(false, true);

		float theDamage = damage.callback(game, caster, localStore, castId);

		if (isAttack != null) {
			flags.setAttack(isAttack.callback(game, caster, localStore, castId));
		}
		if (isRanged != null) {
			flags.setRanged(isRanged.callback(game, caster, localStore, castId));
		}
		if (damageInvulnerable != null) {
			flags.setIgnoreInvulnerable(damageInvulnerable.callback(game, caster, localStore, castId));
		}
		if (explodeOnDeath != null) {
			flags.setExplode(explodeOnDeath.callback(game, caster, localStore, castId));
		}
		if (onlyDamageSummons != null) {
			flags.setOnlyDamageSummons(onlyDamageSummons.callback(game, caster, localStore, castId));
		}
		if (this.attackType != null) {
			theAttackType = this.attackType.callback(game, caster, localStore, castId);
		}
		if (this.damageType != null) {
			theDamageType = this.damageType.callback(game, caster, localStore, castId);
		}
		if (ignoreLTEZero == null || !ignoreLTEZero.callback(game, caster, localStore, castId) || theDamage > 0) {
			target.callback(game, caster, localStore, castId).damage(game,
					source.callback(game, caster, localStore, castId), flags, theAttackType, theDamageType,
					CWeaponSoundTypeJass.WHOKNOWS.name(), damage.callback(game, caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String attackExpression = "false";
		String rangedExpression = "true";
		String attackTypeExpression = "ATTACK_TYPE_NORMAL";
		String damageTypeExpression = "DAMAGE_TYPE_MAGIC";
		if (this.isAttack != null) {
			attackExpression = this.isAttack.generateJassEquivalent(jassTextGenerator);
		}
		if (this.isRanged != null) {
			rangedExpression = this.isRanged.generateJassEquivalent(jassTextGenerator);
		}
		if (this.attackType != null) {
			attackTypeExpression = this.attackType.generateJassEquivalent(jassTextGenerator);
		}
		if (this.damageType != null) {
			damageTypeExpression = this.damageType.generateJassEquivalent(jassTextGenerator);
		}
		if (this.ignoreLTEZero == null) {
			return "UnitDamageTarget(" + this.source.generateJassEquivalent(jassTextGenerator) + ", "
					+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
					+ this.damage.generateJassEquivalent(jassTextGenerator) + ", " + attackExpression + ", "
					+ rangedExpression + ", " + attackTypeExpression + ", " + damageTypeExpression + ", "
					+ "WEAPON_TYPE_WHOKNOWS)";
		}
		return "UnitDamageTargetAU(" + this.source.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.damage.generateJassEquivalent(jassTextGenerator) + ", " + attackExpression + ", "
				+ rangedExpression + ", " + attackTypeExpression + ", " + damageTypeExpression + ", "
				+ this.ignoreLTEZero.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
