package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.defaultbuffs;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABGenericTimedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABBuffStun extends ABGenericTimedBuff {
	private static StateModBuff stunBuff = new StateModBuff(StateModBuffType.STUN, 1);

	private NonStackingFx fx;
	private SimulationRenderComponent sfx;
	private SimulationRenderComponent lsfx;

	public ABBuffStun(final int handleId, final War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, final float duration, final boolean leveled) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, false, leveled, false, false);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		unit.addStateModBuff(stunBuff);
		unit.computeUnitState(game, stunBuff.getBuffType());
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.getAlias() != null) {
			this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), CEffectType.TARGET);
			this.sfx = game.unitSoundEffectEvent(unit, getAlias());
			this.lsfx = game.unitLoopSoundEffectEvent(unit, getAlias());
		}
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.removeStateModBuff(stunBuff);
		unit.computeUnitState(game, stunBuff.getBuffType());
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		if (this.sfx != null) {
			this.sfx.remove();
		}
		if (this.lsfx != null) {
			this.lsfx.remove();
		}
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
		// Do Nothing
	}

}
