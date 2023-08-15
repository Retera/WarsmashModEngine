package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;

public class ABTimedArtBuff extends ABGenericTimedBuff {
	private NonStackingFx fx;
	
	public ABTimedArtBuff(int handleId, War3ID alias, float duration, boolean showIcon) {
		this(handleId, alias, duration);
		this.setIconShowing(showIcon);
	}

	public ABTimedArtBuff(int handleId, War3ID alias, float duration) {
		super(handleId, alias, duration, false);
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		if(this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
		this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), CEffectType.TARGET);
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		if (this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
		}
	}

}
