package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CAbilityAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityBrilliance extends CAbilityAuraBase {

	private float manaRegenerationIncrease;
	private boolean percentBonus;

	public CAbilityBrilliance(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateAuraData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.manaRegenerationIncrease =
				worldEditorAbility.getFieldAsFloat(AbilityFields.BrillianceAura.MANA_REGENERATION_INCREASE, level);
		this.percentBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.BrillianceAura.PERCENT_BONUS, level);
	}

	@Override
	protected CBuffAuraBase createBuff(int handleId, CUnit source, CUnit enumUnit) {
		return new CBuffBrilliance(handleId, getBuffId(), !this.percentBonus ? this.manaRegenerationIncrease :
				(enumUnit.getCurrentDefenseDisplay() * this.manaRegenerationIncrease));
	}
}
