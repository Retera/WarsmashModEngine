package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPassiveSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityBash extends CAbilityPassiveSpellBase {
	private float critChance;

	public CAbilityBash(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.critChance = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
	}

}
