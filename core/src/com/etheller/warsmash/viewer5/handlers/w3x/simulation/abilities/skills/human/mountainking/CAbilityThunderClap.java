package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffSlow;
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

	public CAbilityThunderClap(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.thunderclap;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.attackSpeedReductionPercent = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
		this.movementSpeedReductionPercent = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		simulation.getWorldCollision().enumUnitsInRange(caster.getX(), caster.getY(), areaOfEffect, (enumUnit) -> {
			if (!enumUnit.isUnitAlly(simulation.getPlayer(caster.getPlayerIndex()))
					&& enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
				enumUnit.add(simulation,
						new CBuffSlow(simulation.getHandleIdAllocator().createId(), CAbilityThunderClap.this.buffId,
								getDurationForTarget(enumUnit), attackSpeedReductionPercent,
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
