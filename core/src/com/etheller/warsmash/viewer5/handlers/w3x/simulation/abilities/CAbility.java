package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public interface CAbility extends CAbilityView {
	/* should fire when ability added to unit */
	void onAdd(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit */
	void onRemove(CSimulation game, CUnit unit);

	void onOrder(CSimulation game, CUnit caster, CWidget target, boolean queue);

	void onOrder(CSimulation game, CUnit caster, Vector2 point, boolean queue);

	void onOrderNoTarget(CSimulation game, CUnit caster, boolean queue);

	int getOrderId();
}
