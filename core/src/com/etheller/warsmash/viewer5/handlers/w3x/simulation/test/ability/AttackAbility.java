package com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.ability;

import java.awt.Point;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.behavior.AttackTarget;

public class AttackAbility implements IAbility {

	@Override
	public void execute(final CUnit caster, final int orderId, final CWidget target, final Point targetPoint) {
		if (target != null) {
			new AttackTarget(caster, target);
		}
		else if (targetPoint != null) {
			if (orderId == OrderIds.attackground) {
				// TODO some stuff
			}
			else if (orderId == OrderIds.attack) {

			}
		}
	}

}
