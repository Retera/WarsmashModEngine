package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.sappers;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityUnitOrPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityKaboom extends CAbilityUnitOrPointTargetSpellBase {

	private float fullDamageRadius;
	private float fullDamageAmount;
	private float partialDamageAmount;
	private float partialDamageRadius;
	private boolean explodesOnDeath;
	private float buildingDamageFactor;
	private boolean exploding = false;
	private boolean autoCastOn = false;

	public CAbilityKaboom(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.selfdestruct;
	}

	@Override
	public int getAutoCastOnOrderId() {
		return OrderIds.selfdestructon;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return OrderIds.selfdestructoff;
	}

	@Override
	protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
											AbilityTargetCheckReceiver<CWidget> receiver) {
		if (isAutoCastOn()) {
			this.innerCheckCanTarget(game, unit, getBaseOrderId(), target, receiver);
		}
		else {
			super.innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		fullDamageAmount =
				worldEditorAbility.getFieldAsFloat(AbilityFields.AoeDamageUponDeathSapper.FULL_DAMAGE_AMOUNT, level);
		fullDamageRadius =
				worldEditorAbility.getFieldAsFloat(AbilityFields.AoeDamageUponDeathSapper.FULL_DAMAGE_RADIUS, level);
		partialDamageAmount =
				worldEditorAbility.getFieldAsFloat(AbilityFields.AoeDamageUponDeathSapper.PARTIAL_DAMAGE_AMOUNT,
						level);
		partialDamageRadius =
				worldEditorAbility.getFieldAsFloat(AbilityFields.AoeDamageUponDeathSapper.PARTIAL_DAMAGE_RADIUS,
						level);
		explodesOnDeath = worldEditorAbility.getFieldAsBoolean(AbilityFields.KaboomGoblinSapper.EXPLODES_ON_DEATH,
				level);
		buildingDamageFactor =
				worldEditorAbility.getFieldAsFloat(AbilityFields.KaboomGoblinSapper.BUILDING_DAMAGE_FACTOR, level);

		setCastRange(getCastRange() + 128);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		exploding = true;
		caster.kill(simulation);
		return false;
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		if (explodesOnDeath) {
			exploding = true;
		}
		if (exploding) {
			explode(game, cUnit);
		}
	}

	private void explode(CSimulation simulation, CUnit caster) {
		float radius = StrictMath.max(partialDamageRadius, fullDamageRadius);
		simulation.getWorldCollision().enumUnitsInRange(caster.getX(), caster.getY(), radius, (enumUnit) -> {
			if (enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
				float damageAmount;
				if (caster.canReach(enumUnit, fullDamageRadius)) {
					damageAmount = fullDamageAmount;
				}
				else {
					damageAmount = partialDamageAmount;
				}
				if (enumUnit.isBuilding()) {
					damageAmount *= buildingDamageFactor;
				}
				enumUnit.damage(simulation, caster, CAttackType.SPELLS, CDamageType.DEMOLITION,
						CWeaponSoundTypeJass.WHOKNOWS.name(), damageAmount);
			}
			return false;
		});
	}

	@Override
	public void setAutoCastOn(boolean autoCastOn) {
		this.autoCastOn = autoCastOn;
	}

	@Override
	public boolean isAutoCastOn() {
		return autoCastOn;
	}
}
