package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CExtensibleHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public interface CAbility extends CAbilityView, CExtensibleHandle {
	/* should fire when ability added to unit */
	void onAddDisabled(CSimulation game, CUnit unit);
	
	/* should fire when ability added to unit only if the ability is not disabled at the time */
	void onAdd(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit only if the ability is not disabled at the time */
	void onRemove(CSimulation game, CUnit unit);

	/* should fire when ability removed from unit */
	void onRemoveDisabled(CSimulation game, CUnit unit);

	void onTick(CSimulation game, CUnit unit);

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

	void setDisabled(boolean disabled, CAbilityDisableType type);

	void setIconShowing(boolean iconShowing);

	void setPermanent(boolean permanent);

	void setItemAbility(CItem item, int slot);

	CItem getItem();

}
