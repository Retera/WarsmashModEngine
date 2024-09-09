package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.COrderButton;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityOrderButtonJass extends COrderButton implements CHandle {
	private final int handleId;
	private Integer checkUsableIdxVtable;
	private Integer checkTargetLocIdxVtable;
	private Integer beginLocIdxVtable;
	private Integer useIdxVtable;
	private Integer onCancelFromQueueIdxVtable;
	private Integer checkTargetIdxVtable;
	private Integer checkTargetUnitIdxVtable;
	private Integer checkTargetItemIdxVtable;
	private Integer checkTargetDestructableIdxVtable;
	private Integer beginIdxVtable;
	private Integer beginUnitIdxVtable;
	private Integer beginItemIdxVtable;
	private Integer beginDestructableIdxVtable;
	private AbilityTargetCheckReceiver targetReceiver;
	private AbilityActivationReceiver usableReceiver;

	public CAbilityOrderButtonJass(final int handleId, final int orderId) {
		super(orderId);
		this.handleId = handleId;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.checkUsableIdxVtable = type.getMethodTableIndex("checkUsable");
		this.checkTargetIdxVtable = type.getMethodTableIndex("checkTarget");
		this.checkTargetUnitIdxVtable = type.getMethodTableIndex("checkTargetUnit");
		this.checkTargetItemIdxVtable = type.getMethodTableIndex("checkTargetItem");
		this.checkTargetDestructableIdxVtable = type.getMethodTableIndex("checkTargetDestructable");
		this.checkTargetLocIdxVtable = type.getMethodTableIndex("checkTargetLoc");
		this.beginIdxVtable = type.getMethodTableIndex("begin");
		this.beginUnitIdxVtable = type.getMethodTableIndex("beginUnit");
		this.beginItemIdxVtable = type.getMethodTableIndex("beginItem");
		this.beginDestructableIdxVtable = type.getMethodTableIndex("beginDestructable");
		this.beginLocIdxVtable = type.getMethodTableIndex("beginLoc");
		this.useIdxVtable = type.getMethodTableIndex("use");
		this.onCancelFromQueueIdxVtable = type.getMethodTableIndex("onCancelFromQueue");
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	public void checkTarget(final CSimulation game, final CUnit unit,
			final CommonTriggerExecutionScope jassAbilityBasicScope, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		this.targetReceiver = receiver;
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), unit));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		final Integer instructionPtr = target.visit(new CWidgetVisitor<Integer>() {
			@Override
			public Integer accept(final CUnit target) {
				arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), target));
				return structValue.getType().getMethodTable()
						.get(CAbilityOrderButtonJass.this.checkTargetUnitIdxVtable);
			}

			@Override
			public Integer accept(final CDestructable target) {
				arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("destructable"), target));
				return structValue.getType().getMethodTable()
						.get(CAbilityOrderButtonJass.this.checkTargetDestructableIdxVtable);
			}

			@Override
			public Integer accept(final CItem target) {
				arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("item"), target));
				return structValue.getType().getMethodTable()
						.get(CAbilityOrderButtonJass.this.checkTargetItemIdxVtable);
			}
		});
		globalScope
				.runThreadUntilCompletion(globalScope.createThread(instructionPtr, arguments, jassAbilityBasicScope));
		this.targetReceiver = null;
	}

	public void checkTargetLoc(final CSimulation game, final CUnit unit,
			final CommonTriggerExecutionScope jassAbilityBasicScope, final AbilityPointTarget target,
			final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		this.targetReceiver = receiver;
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), unit));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("location"), target));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.checkTargetLocIdxVtable);
		globalScope
				.runThreadUntilCompletion(globalScope.createThread(instructionPtr, arguments, jassAbilityBasicScope));
		this.targetReceiver = null;
	}

	public void checkTargetNoTarget(final CSimulation game, final CUnit unit,
			final CommonTriggerExecutionScope jassAbilityBasicScope, final AbilityTargetCheckReceiver<Void> receiver) {
		this.targetReceiver = receiver;
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), unit));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.checkTargetIdxVtable);
		globalScope
				.runThreadUntilCompletion(globalScope.createThread(instructionPtr, arguments, jassAbilityBasicScope));
		this.targetReceiver = null;
	}

	public void checkCanUse(final CSimulation game, final CUnit unit,
			final CommonTriggerExecutionScope jassAbilityBasicScope, final AbilityActivationReceiver delegateReceiver) {
		this.usableReceiver = delegateReceiver;
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), unit));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.checkUsableIdxVtable);
		globalScope
				.runThreadUntilCompletion(globalScope.createThread(instructionPtr, arguments, jassAbilityBasicScope));
		this.usableReceiver = null;
	}

	public void use(final CSimulation game, final CUnit caster, final CommonTriggerExecutionScope jassAbilityBasicScope,
			final AbilityTarget target) {
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), caster));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.useIdxVtable);
		globalScope
				.runThreadUntilCompletion(globalScope.createThread(instructionPtr, arguments, jassAbilityBasicScope));
	}

	public void cancelFromQueue(final CSimulation game, final CUnit unit,
			final CommonTriggerExecutionScope jassAbilityBasicScope) {
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), unit));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.onCancelFromQueueIdxVtable);
		globalScope
				.runThreadUntilCompletion(globalScope.createThread(instructionPtr, arguments, jassAbilityBasicScope));
	}

	public CBehavior begin(final CSimulation game, final CUnit caster,
			final CommonTriggerExecutionScope jassAbilityBasicScope, final CWidget target) {
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), caster));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));

		final Integer instructionPtr = target.visit(new CWidgetVisitor<Integer>() {
			@Override
			public Integer accept(final CUnit target) {
				arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), target));
				return structValue.getType().getMethodTable().get(CAbilityOrderButtonJass.this.beginUnitIdxVtable);
			}

			@Override
			public Integer accept(final CDestructable target) {
				arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("destructable"), target));
				return structValue.getType().getMethodTable()
						.get(CAbilityOrderButtonJass.this.beginDestructableIdxVtable);
			}

			@Override
			public Integer accept(final CItem target) {
				arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("item"), target));
				return structValue.getType().getMethodTable().get(CAbilityOrderButtonJass.this.beginItemIdxVtable);
			}
		});

		final JassThread thread = globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				jassAbilityBasicScope);
		globalScope.runThreadUntilCompletion(thread);
		final JassValue jassReturnValue = globalScope.runThreadUntilCompletionAndReadReturnValue(thread, "begin", null);
		if (jassReturnValue == null) {
			throw new IllegalStateException("A jass based ability did not return behavior!");
		}
		final CBehavior behavior = jassReturnValue.visit(ObjectJassValueVisitor.getInstance());
		return behavior;
	}

	public CBehavior begin(final CSimulation game, final CUnit caster,
			final CommonTriggerExecutionScope jassAbilityBasicScope) {
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), caster));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.beginIdxVtable);
		final JassThread thread = globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				jassAbilityBasicScope);
		globalScope.runThreadUntilCompletion(thread);
		final JassValue jassReturnValue = globalScope.runThreadUntilCompletionAndReadReturnValue(thread, "begin", null);
		if (jassReturnValue == null) {
			throw new IllegalStateException("A jass based ability did not return behavior!");
		}
		final CBehavior behavior = jassReturnValue.visit(ObjectJassValueVisitor.getInstance());
		return behavior;
	}

	public CBehavior beginLoc(final CSimulation game, final CUnit caster,
			final CommonTriggerExecutionScope jassAbilityBasicScope, final AbilityPointTarget point) {
		final GlobalScope globalScope = game.getGlobalScope();
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("unit"), caster));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("ability"),
				jassAbilityBasicScope.getSpellAbility()));
		arguments.add(new HandleJassValue((HandleJassType) globalScope.parseType("location"), point));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.beginLocIdxVtable);
		final JassThread thread = globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				jassAbilityBasicScope);
		globalScope.runThreadUntilCompletion(thread);
		final JassValue jassReturnValue = globalScope.runThreadUntilCompletionAndReadReturnValue(thread, "beginLoc",
				null);
		if (jassReturnValue == null) {
			throw new IllegalStateException("A jass based ability did not return behavior!");
		}
		final CBehavior behavior = jassReturnValue.visit(ObjectJassValueVisitor.getInstance());
		return behavior;
	}

	public AbilityTargetCheckReceiver getTargetReceiver() {
		return this.targetReceiver;
	}

	public AbilityActivationReceiver getUsableReceiver() {
		return this.usableReceiver;
	}

}
