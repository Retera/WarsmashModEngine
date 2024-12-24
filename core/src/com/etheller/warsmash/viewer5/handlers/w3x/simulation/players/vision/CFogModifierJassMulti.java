package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class CFogModifierJassMulti implements CFogModifierJass {
	private final List<CFogModifierJass> modifiers;

	public CFogModifierJassMulti(List<CFogModifierJass> modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public void setEnabled(boolean flag) {
		for (final CFogModifierJass modifier : this.modifiers) {
			modifier.setEnabled(flag);
		}
	}

	@Override
	public void destroy(CSimulation game) {
		for (final CFogModifierJass modifier : this.modifiers) {
			modifier.destroy(game);
		}
	}

}
