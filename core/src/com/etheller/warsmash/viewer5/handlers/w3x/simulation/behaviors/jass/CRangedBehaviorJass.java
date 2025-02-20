package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.jass;

import java.util.LinkedList;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CRangedBehavior;

public class CRangedBehaviorJass extends CBehaviorJass implements CRangedBehavior {
	private Integer isWithinRangeIdxVtable;
	private Integer endMoveIdxVtable;
	private Integer getTargetIdxVtable;

	public CRangedBehaviorJass(final GlobalScope globalScope) {
		super(globalScope);
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.isWithinRangeIdxVtable = type.getMethodTableIndex("isWithinRange");
		this.endMoveIdxVtable = type.getMethodTableIndex("endMove");
		this.getTargetIdxVtable = type.getMethodTableIndex("getTarget");
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return runMethod(this.getGlobalScope(), this.isWithinRangeIdxVtable, "CRangedBehaviorJass.isWithinRange",
				new LinkedList<>(), BooleanJassValueVisitor.getInstance());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
		final LinkedList<JassValue> arguments = new LinkedList<>();
		arguments.add(BooleanJassValue.of(interrupted));
		runMethodReturnNothing(game.getGlobalScope(), this.endMoveIdxVtable, arguments);
	}

	@Override
	public AbilityTarget getTarget() {
		return runMethod(this.getGlobalScope(), this.getTargetIdxVtable, "CRangedBehaviorJass.getTarget",
				new LinkedList<>(), ObjectJassValueVisitor.getInstance());
	}

}
