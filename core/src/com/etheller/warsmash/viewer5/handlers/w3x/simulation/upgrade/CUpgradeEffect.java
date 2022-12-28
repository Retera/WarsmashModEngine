package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface CUpgradeEffect {
	void apply(CSimulation simulation, CUnit unit, int level);

	void unapply(CSimulation simulation, CUnit unit, int level);

	default void apply(CSimulation simulation, int playerIndex, int level) {
	}

	default void unapply(CSimulation simulation, int playerIndex, int level) {
	}

	class Util {
		static int levelValue(int base, int mod, int level) {
			return base + (mod * level);
		}

		static float levelValue(float base, float mod, int level) {
			return base + (mod * level);
		}
	}
}
