package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.jass;

import java.util.LinkedList;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;

public class CBehaviorJass extends CExtensibleHandleAbstract implements CBehavior {
	private final GlobalScope globalScope;
	private Integer updateIdxVtable;
	private Integer beginIdxVtable;
	private Integer endIdxVtable;
	private Integer getHighlightOrderIdIdxVtable;
	private Integer interruptableIdxVtable;
	private Integer getBehaviorCategoryIdxVtable;

	private int highlightOrderId;

	public CBehaviorJass(final GlobalScope globalScope) {
		this.globalScope = globalScope;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.updateIdxVtable = type.getMethodTableIndex("update");
		this.beginIdxVtable = type.getMethodTableIndex("begin");
		this.endIdxVtable = type.getMethodTableIndex("end");
		this.getHighlightOrderIdIdxVtable = type.getMethodTableIndex("getHighlightOrderId");
		this.interruptableIdxVtable = type.getMethodTableIndex("interruptable");
		this.getBehaviorCategoryIdxVtable = type.getMethodTableIndex("getBehaviorCategory");
	}

	@Override
	public CBehavior update(final CSimulation game) {
		final CBehavior returnValue = runMethod(game.getGlobalScope(), this.updateIdxVtable, "CBehaviorJass.update",
				new LinkedList<>());
		return returnValue;
	}

	@Override
	public void begin(final CSimulation game) {
		runMethodReturnNothing(game.getGlobalScope(), this.beginIdxVtable, new LinkedList<>());
		this.highlightOrderId = runMethod(game.getGlobalScope(), this.getHighlightOrderIdIdxVtable,
				"CBehaviorJass.getHighlightOrderId", new LinkedList<>(), IntegerJassValueVisitor.getInstance());
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
		return runMethod(this.globalScope, this.interruptableIdxVtable, "CBehaviorJass.interruptable",
				new LinkedList<>(), BooleanJassValueVisitor.getInstance());
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return runMethod(this.globalScope, this.getBehaviorCategoryIdxVtable, "CBehaviorJass.getBehaviorCategory",
				new LinkedList<>(), ObjectJassValueVisitor.getInstance());
	}

	public GlobalScope getGlobalScope() {
		return this.globalScope;
	}

}
