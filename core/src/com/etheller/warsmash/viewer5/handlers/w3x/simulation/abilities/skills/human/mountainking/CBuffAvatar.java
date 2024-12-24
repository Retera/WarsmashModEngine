package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CBuffAvatar extends CBuffTimed {
	private final int hitPointBonus;
	private final int damageBonus;
	private final float defenseBonus;

	public CBuffAvatar(final int handleId, final War3ID alias, final float duration, final int hitPointBonus,
			final int damageBonus, final float defenseBonus) {
		super(handleId, alias, alias, duration);
		this.hitPointBonus = hitPointBonus;
		this.damageBonus = damageBonus;
		this.defenseBonus = defenseBonus;
	}

	@Override
	public boolean isTimedLifeBar() {
		return true;
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		unit.addMaxLifeRelative(game, this.hitPointBonus);
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() + this.defenseBonus);
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() + this.damageBonus);
		}
		unit.setMagicImmune(true);
		if (unit.getUnitAnimationListener().addSecondaryTag(AnimationTokens.SecondaryTag.ALTERNATE)) {
			unit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.addMaxLifeRelative(game, -this.hitPointBonus);
		unit.setTemporaryDefenseBonus(unit.getTemporaryDefenseBonus() - this.defenseBonus);
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() - this.damageBonus);
		}
		unit.setMagicImmune(false);
		if (unit.getUnitAnimationListener().removeSecondaryTag(AnimationTokens.SecondaryTag.ALTERNATE)) {
			unit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
	}
}
