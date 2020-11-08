package com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.ability;

import java.awt.Point;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.behavior.MoveToPoint;

public class MoveAbility implements IAbility {

	@Override
	public void execute(final CUnit caster, final int orderId, final CWidget targetUnit, final Point targetPoint) {
		if (targetUnit == null) {
			caster.setBehavior(new MoveToPoint(caster, targetPoint));
		}
		else {

		}
	}

}
