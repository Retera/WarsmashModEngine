package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABGroupEffect extends CTimer implements SimulationRenderComponent {

	private AbilityPointTarget loc;
	private War3ID id;
	private CEffectType effectType;

	private float inRad;
	private float jitter;

	private boolean removing = false;

	public ABGroupEffect(AbilityPointTarget loc, float rad, War3ID id, CEffectType effectType) {
		this.loc = loc;
		this.id = id;
		this.effectType = effectType;

		this.inRad = rad / 2;
		this.jitter = rad * 0.4f;
		this.setRepeats(true);
	}

	@Override
	public void remove() {
		this.removing = true;
	}

	private float getJitter(CSimulation game) {
		return this.jitter * (game.getSeededRandom().nextFloat() - 0.5f);
	}

	@Override
	public void onFire(CSimulation game) {
		if (this.removing) {
			this.setRepeats(false);
			game.unregisterTimer(this);
			return;
		}

		int total = 2 + game.getSeededRandom().nextInt(19);
		if (total > 5) {
			total--;
			game.spawnTemporarySpellEffectOnPoint(loc.x + this.getJitter(game), loc.y + this.getJitter(game), 0, id,
					effectType, 0);
		}
		double angleDelta = (Math.PI * 2 / total);
		double startAngle = (Math.PI * 2 * game.getSeededRandom().nextFloat());
		while (total > 0) {
			total--;
			double iAngle = startAngle + angleDelta * total;
			double iX = inRad * Math.cos(iAngle);
			double iY = inRad * Math.sin(iAngle);
			game.spawnTemporarySpellEffectOnPoint((float) (loc.x + this.getJitter(game) + iX),
					(float) (loc.y + this.getJitter(game) + iY), 0, id, effectType, 0);
		}
		this.setTimeoutTime(game.getSeededRandom().nextFloat());
	}

}
