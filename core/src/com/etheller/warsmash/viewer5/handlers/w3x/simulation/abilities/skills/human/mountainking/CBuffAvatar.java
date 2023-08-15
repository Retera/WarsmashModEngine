package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffect;

public class CBuffAvatar extends CBuffTimed {
	private final int hitPointBonus;
	private final int damageBonus;
	private final float defenseBonus;

	public CBuffAvatar(int handleId, War3ID alias, float duration, int hitPointBonus, int damageBonus,
					   float defenseBonus) {
		super(handleId, alias, duration);
		this.hitPointBonus = hitPointBonus;
		this.damageBonus = damageBonus;
		this.defenseBonus = defenseBonus;
	}

	@Override
	public boolean isTimedLifeBar() {
		return true;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		unit.addMaxLifeRelative(game, hitPointBonus);
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() + this.defenseBonus);
		for(CUnitAttack attack: unit.getUnitSpecificAttacks()) {
			attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() + this.damageBonus);
		}
		unit.setMagicImmune(true);
		unit.getUnitAnimationListener().addSecondaryTag(AnimationTokens.SecondaryTag.ALTERNATE);
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		unit.addMaxLifeRelative(game, -hitPointBonus);
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() - this.defenseBonus);
		for(CUnitAttack attack: unit.getUnitSpecificAttacks()) {
			attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() - this.damageBonus);
		}
		unit.setMagicImmune(false);
		unit.getUnitAnimationListener().removeSecondaryTag(AnimationTokens.SecondaryTag.ALTERNATE);
	}
}
