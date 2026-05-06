package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.defaultbuffs;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABGenericTimedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABBuffSlow extends ABGenericTimedBuff {
	private static NonStackingStatBuff STANDARD_ATTACK_DEBUFF;
	private static NonStackingStatBuff STANDARD_MOVE_DEBUFF;

	private NonStackingStatBuff attackSpeedDebuff;
	private NonStackingStatBuff moveSpeedDebuff;

	private NonStackingFx fx;
	private SimulationRenderComponent sfx;
	private SimulationRenderComponent lsfx;

	public ABBuffSlow(final CSimulation game, final int handleId, final War3ID alias, ABLocalDataStore localStore,
			CAbility sourceAbility, CUnit sourceUnit, final float duration, boolean leveled) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, false, leveled, false, false);
		if (STANDARD_ATTACK_DEBUFF == null) {
			STANDARD_ATTACK_DEBUFF = new NonStackingStatBuff(NonStackingStatBuffType.ATKSPD, "genericSlow",
					-1 * game.getGameplayConstants().getFrostAttackSpeedDecrease());
		}
		if (STANDARD_MOVE_DEBUFF == null) {
			STANDARD_MOVE_DEBUFF = new NonStackingStatBuff(NonStackingStatBuffType.MVSPDPCT, "genericSlow",
					-1 * game.getGameplayConstants().getFrostMoveSpeedDecrease());
		}
		this.attackSpeedDebuff = STANDARD_ATTACK_DEBUFF;
		this.moveSpeedDebuff = STANDARD_MOVE_DEBUFF;
	}

	public ABBuffSlow(final int handleId, final War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, final float duration, final float attackSpeedReductionPercent,
			final float moveSpeedReductionPercent, boolean leveled) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, false, leveled, false, false);
		this.attackSpeedDebuff = new NonStackingStatBuff(NonStackingStatBuffType.ATKSPD, "genericSlow",
				-1 * attackSpeedReductionPercent);
		this.moveSpeedDebuff = new NonStackingStatBuff(NonStackingStatBuffType.MVSPDPCT, "genericSlow",
				-1 * moveSpeedReductionPercent);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ABBuffSlow other = (ABBuffSlow) obj;
		return this.attackSpeedDebuff.getValue() == other.attackSpeedDebuff.getValue()
				&& this.moveSpeedDebuff.getValue() == other.moveSpeedDebuff.getValue();
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		unit.addNonStackingStatBuff(game, attackSpeedDebuff);
		unit.addNonStackingStatBuff(game, moveSpeedDebuff);
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.getAlias() != null) {
			this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), CEffectType.TARGET);
			this.sfx = game.unitSoundEffectEvent(unit, getAlias());
			this.lsfx = game.unitLoopSoundEffectEvent(unit, getAlias());
		}

		float[] color = game.getUnitVertexColor(unit);
		color[0] *= 0.6;
		color[1] *= 0.6;
		game.changeUnitVertexColor(unit, color[0], color[1], color[2]);
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.removeNonStackingStatBuff(game, attackSpeedDebuff);
		unit.removeNonStackingStatBuff(game, moveSpeedDebuff);
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.sfx != null) {
			this.sfx.remove();
		}
		if (this.lsfx != null) {
			this.lsfx.remove();
		}
		game.changeUnitVertexColor(unit, 1, 1, 1);
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		// Do Nothing
	}

}
