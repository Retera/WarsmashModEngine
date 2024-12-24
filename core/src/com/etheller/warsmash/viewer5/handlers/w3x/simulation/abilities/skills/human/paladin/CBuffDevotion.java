package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;

public class CBuffDevotion extends CBuffAuraBase {
	private final float armorBonus;

	public CBuffDevotion(int handleId, War3ID alias, float armorBonus) {
		super(handleId, alias, alias);
		this.armorBonus = armorBonus;
	}

	@Override
	public void onBuffAdd(CSimulation game, CUnit unit) {
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() + armorBonus);
	}

	@Override
	public void onBuffRemove(CSimulation game, CUnit unit) {
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() - armorBonus);
	}
}
