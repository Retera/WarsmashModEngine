package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.damage;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
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
	private ABBooleanCallback nonlethal;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;
		CDamageFlags flags = new CGenericDamageFlags(false, true);

		float theDamage = damage.callback(caster, localStore, castId);

		if (isAttack != null) {
			flags.setAttack(isAttack.callback(caster, localStore, castId));
		}
		if (isRanged != null) {
			flags.setRanged(isRanged.callback(caster, localStore, castId));
		}
		if (damageInvulnerable != null) {
			flags.setIgnoreInvulnerable(damageInvulnerable.callback(caster, localStore, castId));
		}
		if (explodeOnDeath != null) {
			flags.setExplode(explodeOnDeath.callback(caster, localStore, castId));
		}
		if (onlyDamageSummons != null) {
			flags.setOnlyDamageSummons(onlyDamageSummons.callback(caster, localStore, castId));
		}
		if (nonlethal != null) {
			flags.setNonlethal(nonlethal.callback(caster, localStore, castId));
		}
		if (this.attackType != null) {
			theAttackType = this.attackType.callback(caster, localStore, castId);
		}
		if (this.damageType != null) {
			theDamageType = this.damageType.callback(caster, localStore, castId);
		}
		if (theDamage > 0 || ignoreLTEZero == null || !ignoreLTEZero.callback(caster, localStore, castId)) {
			target.callback(caster, localStore, castId).damage(localStore.game,
					source.callback(caster, localStore, castId), flags, theAttackType, theDamageType,
					CWeaponSoundTypeJass.WHOKNOWS.name(), damage.callback(caster, localStore, castId));
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
