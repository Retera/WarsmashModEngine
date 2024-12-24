package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public interface CAbilityView extends CHandle {
	void checkCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver);

	void checkCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId, AbilityTargetCheckReceiver<Void> receiver);

	void checkRequirementsMet(CSimulation game, CUnit unit, AbilityActivationReceiver receiver);
	
	boolean isRequirementsMet(CSimulation game, CUnit unit);

	@Override
	int getHandleId();
	
	War3ID getAlias();
	
	War3ID getCode();

	boolean isDisabled();

	boolean isIconShowing();

	boolean isPermanent();
	
	boolean isPhysical();
	
	boolean isUniversal();
	
	CAbilityCategory getAbilityCategory();

	<T> T visit(CAbilityVisitor<T> visitor);
}
