package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CUpgradeEffectSpellLevel implements CUpgradeEffect {
	private int base;
	private int mod;
	private War3ID rawcode;

	public CUpgradeEffectSpellLevel(int base, int mod, War3ID rawcode) {
		this.base = base;
		this.mod = mod;
		this.rawcode = rawcode;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		final CAbilityType<?> abilityType = simulation.getAbilityData().getAbilityType(rawcode);
		if (abilityType != null) {
			for (CAbility ability : unit.getAbilities()) {
				CLevelingAbility abilityByRawcode = ability
						.visit(GetAbilityByRawcodeVisitor.getInstance().reset(rawcode));
				if (abilityByRawcode != null) {
					abilityType.setLevel(simulation, unit, abilityByRawcode, level + 1);
				}
			}
		}
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		final CAbilityType<?> abilityType = simulation.getAbilityData().getAbilityType(rawcode);
		if (abilityType != null) {
			for (CAbility ability : unit.getAbilities()) {
				CLevelingAbility abilityByRawcode = ability
						.visit(GetAbilityByRawcodeVisitor.getInstance().reset(rawcode));
				if (abilityByRawcode != null) {
					abilityType.setLevel(simulation, unit, abilityByRawcode, level);
				}
			}
		}
	}
}
