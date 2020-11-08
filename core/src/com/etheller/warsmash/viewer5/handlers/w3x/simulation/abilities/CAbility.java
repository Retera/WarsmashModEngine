package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public interface CAbility extends CAbilityView {
	/* should fire when ability added to unit */
	void onAdd(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit */
	void onRemove(CSimulation game, CUnit unit);

	/* return false to not do anything, such as for toggling autocast */
	boolean checkBeforeQueue(CSimulation game, CUnit caster, int orderId);

	CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target);

	CBehavior begin(CSimulation game, CUnit caster, int orderId, Vector2 point);

	CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId);

}
