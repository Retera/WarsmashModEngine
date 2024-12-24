package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;

public class CBuffBrilliance extends CBuffAuraBase {
	private final float manaRegenBonus;

	public CBuffBrilliance(int handleId, War3ID alias, float manaRegenBonus) {
		super(handleId, alias, alias);
		this.manaRegenBonus = manaRegenBonus;
	}

	@Override
	public void onBuffAdd(CSimulation game, CUnit unit) {
		unit.setManaRegenBonus(unit.getManaRegenBonus() + manaRegenBonus);
	}

	@Override
	public void onBuffRemove(CSimulation game, CUnit unit) {
		unit.setManaRegenBonus(unit.getManaRegenBonus() - manaRegenBonus);
	}
}
