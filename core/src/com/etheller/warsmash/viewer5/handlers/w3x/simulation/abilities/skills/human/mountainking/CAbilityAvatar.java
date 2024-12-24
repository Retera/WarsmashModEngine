package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class CAbilityAvatar extends CAbilityNoTargetSpellBase {
	private static final War3ID AVATAR_BUFF = War3ID.fromString("BHav");

	private int hitPointBonus;
	private int damageBonus;
	private float defenseBonus;

	public CAbilityAvatar(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.avatar;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		hitPointBonus = (int) StrictMath.floor(worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0));
		damageBonus = (int) StrictMath.floor(worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0));
		defenseBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		setCastingPrimaryTag(AnimationTokens.PrimaryTag.MORPH);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		caster.add(simulation, new CBuffAvatar(simulation.getHandleIdAllocator().createId(), AVATAR_BUFF, getDuration(),
				hitPointBonus, damageBonus, defenseBonus));
		return false;
	}
}
