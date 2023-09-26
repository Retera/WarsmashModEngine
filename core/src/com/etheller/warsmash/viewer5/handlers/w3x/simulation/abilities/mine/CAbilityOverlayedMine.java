package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;

public abstract class CAbilityOverlayedMine extends AbstractGenericNoIconAbility {
	private CUnit parentGoldMineUnit;
	private CAbilityGoldMinable parentGoldMineAbility;

	public CAbilityOverlayedMine(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	public void setParentMine(final CUnit parentGoldMineUnit, final CAbilityGoldMinable parentGoldMineAbility) {
		this.parentGoldMineUnit = parentGoldMineUnit;
		this.parentGoldMineAbility = parentGoldMineAbility;
	}

	public CUnit getParentGoldMineUnit() {
		return this.parentGoldMineUnit;
	}

	public CAbilityGoldMinable getParentGoldMineAbility() {
		return this.parentGoldMineAbility;
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		if (this.parentGoldMineUnit != null) {
			this.parentGoldMineUnit.setHidden(false);
			this.parentGoldMineUnit.setPaused(false);
		}
	}

	public void setGold(final int gold) {
		if (this.parentGoldMineAbility != null) {
			this.parentGoldMineAbility.setGold(gold);
		}
	}

	public int getGold() {
		if (this.parentGoldMineAbility != null) {
			return this.parentGoldMineAbility.getGold();
		}
		return 0;
	}
}
