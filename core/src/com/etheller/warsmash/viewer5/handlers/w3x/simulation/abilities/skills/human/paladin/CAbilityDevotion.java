package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CAbilityAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityDevotion extends CAbilityAuraBase {

	private float armorBonus;
	private boolean percentBonus;

	public CAbilityDevotion(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public void populateAuraData(final GameObject worldEditorAbility, final int level) {
		this.armorBonus = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.percentBonus = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_B + level, 0);
	}

	@Override
	protected CBuffAuraBase createBuff(final int handleId, final CUnit source, final CUnit enumUnit) {
		return new CBuffDevotion(handleId, getBuffId(),
				!this.percentBonus ? this.armorBonus : (enumUnit.getCurrentDefenseDisplay() * this.armorBonus));
	}
}
