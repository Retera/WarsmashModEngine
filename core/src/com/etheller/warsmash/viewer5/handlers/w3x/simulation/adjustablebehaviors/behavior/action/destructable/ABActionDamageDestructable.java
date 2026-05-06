package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.destructable;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CGenericDamageFlags;
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
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		boolean isItAttack = false;
		boolean isItRanged = true;
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;

		if (this.isAttack != null) {
			isItAttack = this.isAttack.callback(caster, localStore, castId);
		}
		if (this.isRanged != null) {
			isItRanged = this.isRanged.callback(caster, localStore, castId);
		}
		if (this.attackType != null) {
			theAttackType = this.attackType.callback(caster, localStore, castId);
		}
		if (this.damageType != null) {
			theDamageType = this.damageType.callback(caster, localStore, castId);
		}

		target.callback(caster, localStore, castId).damage(localStore.game, source.callback(caster, localStore, castId),
				new CGenericDamageFlags(isItAttack, isItRanged), theAttackType, theDamageType,
				CWeaponSoundTypeJass.WHOKNOWS.name(), damage.callback(caster, localStore, castId));
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
