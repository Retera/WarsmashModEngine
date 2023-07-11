package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.manager.MutableObjectData;
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

	public CAbilityAvatar(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.avatar;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		hitPointBonus = (int) StrictMath.floor(worldEditorAbility.getFieldAsFloat(AbilityFields.Avatar.HIT_POINT_BONUS
				, level));
		damageBonus = (int) StrictMath.floor(worldEditorAbility.getFieldAsFloat(AbilityFields.Avatar.DAMAGE_BONUS,
				level));
		defenseBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.Avatar.DEFENSE_BONUS, level);
		setCastingPrimaryTag(AnimationTokens.PrimaryTag.MORPH);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		simulation.createSpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		caster.add(simulation, new CBuffAvatar(simulation.getHandleIdAllocator().createId(), AVATAR_BUFF,
				getDuration(), hitPointBonus, damageBonus, defenseBonus));
		return false;
	}
}
