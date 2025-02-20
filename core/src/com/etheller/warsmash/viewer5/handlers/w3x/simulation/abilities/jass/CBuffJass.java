package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CBuffJass extends AbstractCBuff implements CBuff {
	private final GlobalScope globalScope;
	private CommonTriggerExecutionScope jassAbilityBasicScope;
	private Integer onAddIdxVtable;
	private Integer onRemoveIdxVtable;
	private Integer onDeathIdxVtable;
	private Integer getDurationRemainingIdxVtable;
	private Integer getDurationMaxIdxVtable;
	private Integer isTimedLifeBarIdxVtable;

	public CBuffJass(final int handleId, final War3ID codeId, final War3ID alias, final GlobalScope globalScope) {
		super(handleId, codeId, alias);
		this.globalScope = globalScope;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.onAddIdxVtable = type.getMethodTableIndex("onAdd");
		this.onRemoveIdxVtable = type.getMethodTableIndex("onRemove");
		this.onDeathIdxVtable = type.getMethodTableIndex("onDeath");

		this.getDurationRemainingIdxVtable = type.getMethodTableIndex("getDurationRemaining");
		this.getDurationMaxIdxVtable = type.getMethodTableIndex("getDurationMax");
		this.isTimedLifeBarIdxVtable = type.getMethodTableIndex("isTimedLifeBar");
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.jassAbilityBasicScope = CommonTriggerExecutionScope.jassAbilityBasicScope(this, unit, getAlias());
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) this.globalScope.parseType("unit"), unit));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.onAddIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) this.globalScope.parseType("unit"), unit));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.onRemoveIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	@Override
	public float getDurationRemaining(final CSimulation game, final CUnit unit) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		arguments.add(new HandleJassValue(this.globalScope.getHandleType("unit"), unit));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.getDurationRemainingIdxVtable);
		final JassThread thread = this.globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				this.jassAbilityBasicScope);
		final JassValue value = this.globalScope.runThreadUntilCompletionAndReadReturnValue(thread,
				"getDurationRemaining", IntegerJassValue.ZERO);
		return value.visit(IntegerJassValueVisitor.getInstance());
	}

	@Override
	public float getDurationMax() {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.getDurationMaxIdxVtable);
		final JassThread thread = this.globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				this.jassAbilityBasicScope);
		final JassValue value = this.globalScope.runThreadUntilCompletionAndReadReturnValue(thread, "getDurationMax",
				IntegerJassValue.ZERO);
		return value.visit(IntegerJassValueVisitor.getInstance());
	}

	@Override
	public boolean isTimedLifeBar() {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.isTimedLifeBarIdxVtable);
		final JassThread thread = this.globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				this.jassAbilityBasicScope);
		final JassValue value = this.globalScope.runThreadUntilCompletionAndReadReturnValue(thread, "isTimedLifeBar",
				BooleanJassValue.FALSE);
		return value.visit(BooleanJassValueVisitor.getInstance());
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit unit) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue(this.globalScope.getHandleType("unit"), unit));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.onDeathIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

}
