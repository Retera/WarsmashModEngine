package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABTimedArtBuff extends ABGenericTimedBuff {
	private NonStackingFx fx;
	private SimulationRenderComponent sfx;
	private SimulationRenderComponent lsfx;
	private CEffectType artType = CEffectType.TARGET;
	
	public ABTimedArtBuff(int handleId, War3ID alias, float duration, boolean showIcon) {
		this(handleId, alias, duration);
		this.setIconShowing(showIcon);
	}

	public ABTimedArtBuff(int handleId, War3ID alias, float duration) {
		super(handleId, alias, duration, false);
	}
	
	public void setArtType(CEffectType artType) {
		this.artType = artType;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		if(this.fx != null) {
			unit.removeNonStackingFx(game, this.fx);
			this.fx = null;
		}
		if (this.getAlias() != null) {
			this.fx = unit.addNonStackingFx(game, getAlias().asStringValue(), getAlias(), artType);
			this.sfx = game.unitSoundEffectEvent(unit, getAlias());
			this.lsfx = game.unitLoopSoundEffectEvent(unit, getAlias());
		}
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
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
	}

}
