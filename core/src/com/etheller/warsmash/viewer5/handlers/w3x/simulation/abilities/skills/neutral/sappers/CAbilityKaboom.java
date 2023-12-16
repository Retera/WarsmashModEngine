package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.sappers;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityUnitOrPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityKaboom extends CAbilityUnitOrPointTargetSpellBase implements CAutocastAbility {

	private float fullDamageRadius;
	private float fullDamageAmount;
	private float partialDamageAmount;
	private float partialDamageRadius;
	private boolean explodesOnDeath;
	private float buildingDamageFactor;
	private boolean exploding = false;
	private boolean autoCastOn = false;

	public CAbilityKaboom(final int handleId, final War3ID alias) {
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
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (isAutoCastOn()) {
			this.innerCheckCanTarget(game, unit, getBaseOrderId(), target, receiver);
		}
		else {
			super.innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		fullDamageAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		fullDamageRadius = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		partialDamageAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
		partialDamageRadius = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		explodesOnDeath = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_F + level, 0);
		buildingDamageFactor = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_E + level, 0);

		setCastRange(getCastRange() + 128);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		exploding = true;
		caster.kill(simulation);
		return false;
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		if (explodesOnDeath) {
			exploding = true;
		}
		if (exploding) {
			explode(game, cUnit);
		}
	}

	private void explode(final CSimulation simulation, final CUnit caster) {
		final float radius = StrictMath.max(partialDamageRadius, fullDamageRadius);
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
				enumUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.DEMOLITION,
						CWeaponSoundTypeJass.WHOKNOWS.name(), damageAmount);
			}
			return false;
		});
	}

	@Override
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autoCastOn = autoCastOn;
		caster.setAutocastAbility(autoCastOn ? this : null);
	}

	@Override
	public boolean isAutoCastOn() {
		return autoCastOn;
	}

	@Override
	public void setAutoCastOff() {
		this.autoCastOn = false;
	}

	@Override
	public AutocastType getAutocastType() {
		return AutocastType.NEARESTENEMY;
	}


	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		this.checkCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanAutoTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}
}
