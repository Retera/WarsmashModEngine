package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public interface CAbilityView {
	void checkCanUse(CSimulation game, CUnit unit, AbilityActivationReceiver receiver);

	void checkCanTarget(CSimulation game, CUnit unit, CWidget target, AbilityTargetCheckReceiver<CWidget> receiver);

	void checkCanTarget(CSimulation game, CUnit unit, Vector2 target, AbilityTargetCheckReceiver<Vector2> receiver);

	void checkCanTargetNoTarget(CSimulation game, CUnit unit, AbilityTargetCheckReceiver<Void> receiver);

}
