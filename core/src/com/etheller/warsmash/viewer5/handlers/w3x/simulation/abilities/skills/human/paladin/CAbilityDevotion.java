package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CAbilityAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityDevotion extends CAbilityAuraBase {

	private float armorBonus;
	private boolean percentBonus;

	public CAbilityDevotion(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateAuraData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.armorBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.DevotionAura.ARMOR_BONUS, level);
		this.percentBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.DevotionAura.PERCENT_BONUS, level);
	}

	@Override
	protected CBuffAuraBase createBuff(int handleId, CUnit source, CUnit enumUnit) {
		return new CBuffDevotion(handleId, getBuffId(), !this.percentBonus ? this.armorBonus : (enumUnit.getCurrentDefenseDisplay() * this.armorBonus));
	}
}
