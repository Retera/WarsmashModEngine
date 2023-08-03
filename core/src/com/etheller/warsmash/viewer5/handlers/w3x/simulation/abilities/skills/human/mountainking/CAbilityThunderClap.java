package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffSlow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class CAbilityThunderClap extends CAbilityNoTargetSpellBase {

	private float damage;
	private float areaOfEffect;
	private War3ID buffId;
	private float attackSpeedReductionPercent;
	private float movementSpeedReductionPercent;

	public CAbilityThunderClap(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.thunderclap;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.ThunderClap.AOE_DAMAGE, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.attackSpeedReductionPercent =
				worldEditorAbility.getFieldAsFloat(AbilityFields.ThunderClap.ATTACK_SPEED_REDUCTION_PERCENT, level);
		this.movementSpeedReductionPercent =
				worldEditorAbility.getFieldAsFloat(AbilityFields.ThunderClap.MOVEMENT_SPEED_REDUCTION_PERCENT, level);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		simulation.getWorldCollision().enumUnitsInRange(caster.getX(), caster.getY(), areaOfEffect, (enumUnit) -> {
			if (!enumUnit.isUnitAlly(simulation.getPlayer(caster.getPlayerIndex())) && enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
				enumUnit.add(simulation, new CBuffSlow(simulation.getHandleIdAllocator().createId(),
						CAbilityThunderClap.this.buffId, getDurationForTarget(enumUnit), attackSpeedReductionPercent,
						movementSpeedReductionPercent));
				enumUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.UNIVERSAL,
						CWeaponSoundTypeJass.WHOKNOWS.name(), CAbilityThunderClap.this.damage);
			}
			return false;
		});
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		return false;
	}
}
