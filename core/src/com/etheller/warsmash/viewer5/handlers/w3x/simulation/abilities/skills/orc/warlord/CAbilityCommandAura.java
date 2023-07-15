package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.warlord;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CAbilityAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityCommandAura extends CAbilityAuraBase {

	private float attackDamageIncrease;
	private boolean flatBonus;
	private boolean meleeBonus;
	private boolean rangedBonus;

	public CAbilityCommandAura(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	protected CBuffAuraBase createBuff(int handleId, CUnit source, CUnit enumUnit) {
		return null;
	}

	@Override
	public void populateAuraData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		attackDamageIncrease = worldEditorAbility.getFieldAsFloat(AbilityFields.TrueshotAura.DAMAGE_BONUS_PERCENT, level);
		flatBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.TrueshotAura.FLAT_BONUS, level);
		meleeBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.TrueshotAura.MELEE_BONUS, level);
		rangedBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.TrueshotAura.RANGED_BONUS, level);
	}
}
