package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import java.util.UUID;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class CUpgradeEffectAttackSpeed implements CUpgradeEffect {
	private final float base;
	private final float mod;
	
	private NonStackingStatBuff buff;

	public CUpgradeEffectAttackSpeed(final float base, final float mod) {
		this.base = base;
		this.mod = mod;
		this.buff = new NonStackingStatBuff(NonStackingStatBuffType.ATKSPD, UUID.randomUUID().toString(), base);
	}

	@Override
	public void apply(final CSimulation simulation, final CUnit unit, final int level) {
		this.buff.setValue(base + mod * level);
		unit.addNonStackingStatBuff(buff);
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}

	@Override
	public void unapply(final CSimulation simulation, final CUnit unit, final int level) {
		this.buff.setValue(base + mod * level);
		unit.removeNonStackingStatBuff(buff);
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}
}
