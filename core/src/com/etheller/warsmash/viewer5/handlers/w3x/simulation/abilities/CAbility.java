package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public interface CAbility extends CAbilityView {
	/* should fire when ability added to unit */
	void onAdd(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit */
	void onRemove(CSimulation game, CUnit unit);

	void onTick(CSimulation game, CUnit unit);

	default void onBeforeDeath(CSimulation game, CUnit cUnit) {};

	void onDeath(CSimulation game, CUnit cUnit);

	/*
	 * should fire for "permanent" abilities that are kept across unit type change
	 */
	void onSetUnitType(CSimulation game, CUnit cUnit);

	void onCancelFromQueue(CSimulation game, CUnit unit, int orderId);

	/* return false to not do anything, such as for toggling autocast */
	boolean checkBeforeQueue(CSimulation game, CUnit caster, int orderId, AbilityTarget target);

	CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target);

	CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point);

	CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId);

	void setDisabled(boolean disabled);

	void setIconShowing(boolean iconShowing);

	void setPermanent(boolean permanent);

}
