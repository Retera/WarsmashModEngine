package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public class CJassProjectile extends CProjectile {
	private Integer onHitIdxVtable;

	public CJassProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			final boolean homingEnabled, final CUnit source) {
		super(x, y, speed, target, homingEnabled, source);
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.onHitIdxVtable = type.getMethodTableIndex("onHit");
	}

	@Override
	protected void onHitTarget(final CSimulation game) {
		final List<JassValue> arguments = new ArrayList<>();
		final GlobalScope globalScope = game.getGlobalScope();
		arguments.add(new HandleJassValue(globalScope.getHandleType("abilitytarget"), getTarget()));
		runMethodReturnNothing(globalScope, this.onHitIdxVtable, arguments);
	}
}
