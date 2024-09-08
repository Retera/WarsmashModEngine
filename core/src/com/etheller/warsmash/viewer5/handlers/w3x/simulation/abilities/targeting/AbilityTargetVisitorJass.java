package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class AbilityTargetVisitorJass extends CExtensibleHandleAbstract implements CHandle, AbilityTargetVisitor<Void> {
	private final int handleId;
	private Integer visitUnitIdxVtable;
	private Integer visitItemIdxVtable;
	private Integer visitDestIdxVtable;
	private Integer visitLocIdxVtable;

	public AbilityTargetVisitorJass(final int handleId) {
		this.handleId = handleId;
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

	@Override
	public Void accept(final AbilityPointTarget target) {
		return null;
	}

	@Override
	public Void accept(final CUnit target) {
		return null;
	}

	@Override
	public Void accept(final CDestructable target) {
		return null;
	}

	@Override
	public Void accept(final CItem target) {
		return null;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}
