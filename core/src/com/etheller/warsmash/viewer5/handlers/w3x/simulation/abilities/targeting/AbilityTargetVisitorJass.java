package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class AbilityTargetVisitorJass extends CExtensibleHandleAbstract implements CHandle, AbilityTargetVisitor<Void> {
	private final int handleId;
	private final GlobalScope globalScope;
	private TriggerExecutionScope executionScope = TriggerExecutionScope.EMPTY;
	private Integer visitUnitIdxVtable;
	private Integer visitItemIdxVtable;
	private Integer visitDestIdxVtable;
	private Integer visitLocIdxVtable;

	public AbilityTargetVisitorJass(final int handleId, final GlobalScope globalScope) {
		this.handleId = handleId;
		this.globalScope = globalScope;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.visitUnitIdxVtable = type.getMethodTableIndex("visitUnit");
		this.visitItemIdxVtable = type.getMethodTableIndex("visitItem");
		this.visitDestIdxVtable = type.getMethodTableIndex("visitDest");
		this.visitLocIdxVtable = type.getMethodTableIndex("visitLoc");
	}

	public AbilityTargetVisitorJass reset(final TriggerExecutionScope executionScope) {
		this.executionScope = executionScope;
		return this;
	}

	@Override
	public Void accept(final AbilityPointTarget target) {
		final List<JassValue> arguments = new ArrayList<>();
		arguments.add(new HandleJassValue(this.globalScope.getHandleType("location"), target));
		runMethodReturnNothing(this.globalScope, this.visitLocIdxVtable, arguments);
		return null;
	}

	@Override
	public Void accept(final CUnit target) {
		final List<JassValue> arguments = new ArrayList<>();
		arguments.add(new HandleJassValue(this.globalScope.getHandleType("unit"), target));
		runMethodReturnNothing(this.globalScope, this.visitUnitIdxVtable, arguments);
		return null;
	}

	@Override
	public Void accept(final CDestructable target) {
		final List<JassValue> arguments = new ArrayList<>();
		arguments.add(new HandleJassValue(this.globalScope.getHandleType("destructable"), target));
		runMethodReturnNothing(this.globalScope, this.visitDestIdxVtable, arguments);
		return null;
	}

	@Override
	public Void accept(final CItem target) {
		final List<JassValue> arguments = new ArrayList<>();
		arguments.add(new HandleJassValue(this.globalScope.getHandleType("location"), target));
		runMethodReturnNothing(this.globalScope, this.visitItemIdxVtable, arguments);
		return null;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}
