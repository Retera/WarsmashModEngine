package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.jass;

import java.util.LinkedList;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.util.CExtensibleHandle;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;

public class CAbstractRangedBehaviorJass extends CAbstractRangedBehavior implements CBehavior, CExtensibleHandle {
	private final GlobalScope globalScope;
	private StructJassValue structJassValue;
	private Integer isWithinRangeIdxVtable;
	private Integer endMoveIdxVtable;
	private Integer updateIdxVtable;
	private Integer beginIdxVtable;
	private Integer endIdxVtable;
	private Integer getHighlightOrderIdIdxVtable;
	private Integer interruptableIdxVtable;
	private Integer getBehaviorCategoryIdxVtable;
	private Integer updateOnInvalidTargetIdxVtable;
	private Integer checkTargetStillValidIdxVtable;
	private Integer resetBeforeMovingIdxVtable;

	private int highlightOrderId;

	public CAbstractRangedBehaviorJass(final CUnit unit, final GlobalScope globalScope) {
		super(unit);
		this.globalScope = globalScope;
	}

	public final CBehavior resetNative(final CSimulation game, final AbilityTarget target) {
		return innerReset(game, target);
	}

	public final CBehavior resetNative(final CSimulation game, final AbilityTarget target,
			final boolean disableCollision) {
		return innerReset(game, target, disableCollision);
	}

	@Override
	public StructJassValue getStructValue() {
		return this.structJassValue;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		this.structJassValue = structJassValue;
		final StructJassType type = structJassValue.getType();

		this.isWithinRangeIdxVtable = type.getMethodTableIndex("isWithinRange");
		this.endMoveIdxVtable = type.getMethodTableIndex("endMove");

		this.updateIdxVtable = type.getMethodTableIndex("update");
		this.beginIdxVtable = type.getMethodTableIndex("begin");
		this.endIdxVtable = type.getMethodTableIndex("end");
		this.getHighlightOrderIdIdxVtable = type.getMethodTableIndex("getHighlightOrderId");
		this.interruptableIdxVtable = type.getMethodTableIndex("interruptable");
		this.getBehaviorCategoryIdxVtable = type.getMethodTableIndex("getBehaviorCategory");

		this.updateOnInvalidTargetIdxVtable = type.getMethodTableIndex("updateOnInvalidTarget");
		this.checkTargetStillValidIdxVtable = type.getMethodTableIndex("isTargetStillValid");
		this.resetBeforeMovingIdxVtable = type.getMethodTableIndex("resetBeforeMoving");
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return runMethod(this.globalScope, this.isWithinRangeIdxVtable, "CAbstractRangedBehaviorJass.isWithinRange",
				new LinkedList<>(), BooleanJassValueVisitor.getInstance());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
		final LinkedList<JassValue> arguments = new LinkedList<>();
		arguments.add(BooleanJassValue.of(interrupted));
		runMethodReturnNothing(game.getGlobalScope(), this.endMoveIdxVtable, arguments);
	}

	@Override
	public void begin(final CSimulation game) {
		runMethodReturnNothing(game.getGlobalScope(), this.beginIdxVtable, new LinkedList<>());
		this.highlightOrderId = runMethod(game.getGlobalScope(), this.getHighlightOrderIdIdxVtable,
				"CAbstractRangedBehaviorJass.getHighlightOrderId", new LinkedList<>(),
				IntegerJassValueVisitor.getInstance());
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		final LinkedList<JassValue> arguments = new LinkedList<>();
		arguments.add(BooleanJassValue.of(interrupted));
		runMethodReturnNothing(game.getGlobalScope(), this.endIdxVtable, arguments);
	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	public boolean interruptable() {
		return runMethod(this.globalScope, this.interruptableIdxVtable, "CAbstractRangedBehaviorJass.interruptable",
				new LinkedList<>(), BooleanJassValueVisitor.getInstance());
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return runMethod(this.globalScope, this.getBehaviorCategoryIdxVtable,
				"CAbstractRangedBehaviorJass.getBehaviorCategory", new LinkedList<>(),
				ObjectJassValueVisitor.getInstance());
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final LinkedList<JassValue> arguments = new LinkedList<>();
		arguments.add(BooleanJassValue.of(withinFacingWindow));
		final CBehavior returnValue = runMethod(simulation.getGlobalScope(), this.updateIdxVtable,
				"CAbstractRangedBehaviorJass.update", arguments);
		return returnValue;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return runMethod(simulation.getGlobalScope(), this.updateOnInvalidTargetIdxVtable,
				"CAbstractRangedBehaviorJass.updateOnInvalidTarget", new LinkedList<>());
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return runMethod(this.globalScope, this.checkTargetStillValidIdxVtable,
				"CAbstractRangedBehaviorJass.checkTargetStillValid", new LinkedList<>(),
				BooleanJassValueVisitor.getInstance());
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		final LinkedList<JassValue> arguments = new LinkedList<>();
		runMethodReturnNothing(this.globalScope, this.resetBeforeMovingIdxVtable, arguments);
	}

	public CUnit getUnit() {
		return this.unit;
	}

}
