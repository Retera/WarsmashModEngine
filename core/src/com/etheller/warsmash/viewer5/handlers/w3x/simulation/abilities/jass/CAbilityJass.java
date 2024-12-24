package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.util.CExtensibleHandle;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityDisableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.COrderButton;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.COrderButton.JassOrderButtonType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;

public class CAbilityJass extends AbstractGenericAliasedAbility implements CExtensibleHandle {
	private final List<CAbilityOrderButtonJass> orderButtons;
	private CommonTriggerExecutionScope jassAbilityBasicScope;
	private Integer onAddIdxVtable;
	private Integer onRemoveIdxVtable;
	private Integer onSetUnitTypeIdxVtable;
	private Integer setDisabledIdxVtable;
	private Integer setIconShowingIdxVtable;
	private Integer setPermanentIdxVtable;
	private Integer populateIdxVtable;
	private Integer getAbilityCategoryIdxVtable;

	private boolean physical;
	private boolean universal;
	private boolean enabledWhileUnderConstruction;
	private boolean enabledWhileUpgrading;
	private final GlobalScope globalScope;
	private War3ID code;

	public CAbilityJass(final int handleId, final War3ID alias, final GlobalScope globalScope) {
		super(handleId, War3ID.fromString("JASS"), alias);
		this.globalScope = globalScope;
		this.orderButtons = new ArrayList<>();
	}

	public CommonTriggerExecutionScope getJassAbilityBasicScope() {
		return this.jassAbilityBasicScope;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		super.setStructValue(structJassValue);
		final StructJassType type = structJassValue.getType();
		this.onAddIdxVtable = type.getMethodTableIndex("onAdd");
		this.onRemoveIdxVtable = type.getMethodTableIndex("onRemove");
		this.onSetUnitTypeIdxVtable = type.getMethodTableIndex("onSetUnitType");

		this.setDisabledIdxVtable = type.getMethodTableIndex("onSetDisabled");
		this.setIconShowingIdxVtable = type.getMethodTableIndex("onSetIconShowing");
		this.setPermanentIdxVtable = type.getMethodTableIndex("onSetPermanent");

		this.populateIdxVtable = type.getMethodTableIndex("populate");

		this.getAbilityCategoryIdxVtable = type.getMethodTableIndex("getAbilityCategory");
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
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if (orderButton.getOrderId() == orderId) {
				orderButton.cancelFromQueue(game, unit, this.jassAbilityBasicScope);
			}
		}
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if ((orderButton.getType() == JassOrderButtonType.UNIT_TARGET)
					|| (orderButton.getType() == JassOrderButtonType.UNIT_OR_POINT_TARGET)) {
				if (orderButton.getOrderId() == orderId) {
					// TODO maybe something fancier here to make sure we have the correct order id
					return orderButton.begin(game, caster, this.jassAbilityBasicScope, target);
				}
			}
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if ((orderButton.getType() == JassOrderButtonType.POINT_TARGET)
					|| (orderButton.getType() == JassOrderButtonType.UNIT_OR_POINT_TARGET)) {
				if (orderButton.getOrderId() == orderId) {
					// TODO maybe something fancier here to make sure we have the correct order id
					return orderButton.beginLoc(game, caster, this.jassAbilityBasicScope, point);
				}
			}
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if ((orderButton.getType() == JassOrderButtonType.INSTANT_NO_TARGET)
					|| (orderButton.getType() == JassOrderButtonType.INSTANT_NO_TARGET_NO_INTERRUPT)) {
				if (orderButton.getOrderId() == orderId) {
					// TODO maybe something fancier here to make sure we have the correct order id
					return orderButton.begin(game, caster, this.jassAbilityBasicScope);
				}
			}
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		final RecordingAbilityTargetCheckReceiver<CWidget> delegateReceiver = RecordingAbilityTargetCheckReceiver
				.getInstance();
		String errorMessage = null;
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if ((orderButton.getType() == JassOrderButtonType.UNIT_TARGET)
					|| (orderButton.getType() == JassOrderButtonType.UNIT_OR_POINT_TARGET)) {
				if (orderButton.getOrderId() == orderId) {
					orderButton.checkTarget(game, unit, this.jassAbilityBasicScope, target, delegateReceiver.reset());
					if (delegateReceiver.getTarget() != null) {
						receiver.targetOk(target);
						return;
					}
					if (delegateReceiver.getCommandStringErrorKey() != null) {
						errorMessage = delegateReceiver.getCommandStringErrorKey();
					}
				}
			}
		}
		if (errorMessage != null) {
			receiver.targetCheckFailed(errorMessage);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		final RecordingAbilityTargetCheckReceiver<AbilityPointTarget> delegateReceiver = RecordingAbilityTargetCheckReceiver
				.getInstance();
		String errorMessage = null;
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if ((orderButton.getType() == JassOrderButtonType.POINT_TARGET)
					|| (orderButton.getType() == JassOrderButtonType.UNIT_OR_POINT_TARGET)) {
				if (orderButton.getOrderId() == orderId) {
					orderButton.checkTargetLoc(game, unit, this.jassAbilityBasicScope, target,
							delegateReceiver.reset());
					if (delegateReceiver.getTarget() != null) {
						receiver.targetOk(target);
						return;
					}
					if (delegateReceiver.getCommandStringErrorKey() != null) {
						errorMessage = delegateReceiver.getCommandStringErrorKey();
					}
				}
			}
		}
		if (errorMessage != null) {
			receiver.targetCheckFailed(errorMessage);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		final RecordingAbilityTargetCheckReceiver<Void> delegateReceiver = RecordingAbilityTargetCheckReceiver
				.getInstance();
		String errorMessage = null;
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if ((orderButton.getType() == JassOrderButtonType.INSTANT_NO_TARGET)
					|| (orderButton.getType() == JassOrderButtonType.INSTANT_NO_TARGET_NO_INTERRUPT)) {
				if (orderButton.getOrderId() == orderId) {
					orderButton.checkTargetNoTarget(game, unit, this.jassAbilityBasicScope, delegateReceiver.reset());
					if (delegateReceiver.isTargetOk()) {
						receiver.targetOk(delegateReceiver.getTarget());
						return;
					}
					if (delegateReceiver.getCommandStringErrorKey() != null) {
						errorMessage = delegateReceiver.getCommandStringErrorKey();
					}
				}
			}
		}
		if (errorMessage != null) {
			receiver.targetCheckFailed(errorMessage);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		CAbilityOrderButtonJass firstFailButton = null;
		final BooleanAbilityActivationReceiver delegateReceiver = BooleanAbilityActivationReceiver.INSTANCE;
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if (orderButton.getType() != JassOrderButtonType.PASSIVE) {
				if (orderButton.getOrderId() == orderId) {
					orderButton.checkCanUse(game, unit, this.jassAbilityBasicScope, delegateReceiver);
					if (delegateReceiver.isOk()) {
						receiver.useOk();
						return;
					}
					else {
						firstFailButton = orderButton;
					}
				}
			}
		}
		if (firstFailButton != null) {
			firstFailButton.checkCanUse(game, unit, this.jassAbilityBasicScope, receiver);
		}
		else {
			receiver.notAnActiveAbility();
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if (orderButton.getType() == JassOrderButtonType.INSTANT_NO_TARGET_NO_INTERRUPT) {
				if (orderButton.getOrderId() == orderId) {
					orderButton.use(game, caster, this.jassAbilityBasicScope, target);
					return false;
				}
			}
		}
		if (orderId != 0) {
			for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
				if (orderButton.getAutoCastOrderId() == orderId) {
					orderButton.setAutoCastActive(true);
					return false;
				}
				else if (orderButton.getAutoCastUnOrderId() == orderId) {
					orderButton.setAutoCastActive(false);
					return false;
				}
			}
		}
		return true;
	}

	public COrderButton getOrderCommandCardIcon(final CSimulation simulation, final CUnit simulationUnit,
			final int activeCommandOrderId) {
		for (final CAbilityOrderButtonJass orderButton : this.orderButtons) {
			if (orderButton.getOrderId() == activeCommandOrderId) {
				return orderButton;
			}
		}
		return null;
	}

	@Override
	public boolean isPhysical() {
		return this.physical;
	}

	@Override
	public boolean isUniversal() {
		return this.universal;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.getAbilityCategoryIdxVtable);
		final JassThread thread = this.globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
				this.jassAbilityBasicScope);
		final JassValue value = this.globalScope.runThreadUntilCompletionAndReadReturnValue(thread,
				"getAbilityCategory", null);
		if (value == null) {
			return CAbilityCategory.SPELL;
		}
		return value.visit(ObjectJassValueVisitor.getInstance());
	}

	public void populate(final GameObject abilityEditorData, final int level) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments
				.add(new HandleJassValue((HandleJassType) this.globalScope.parseType("gameobject"), abilityEditorData));
		arguments.add(IntegerJassValue.of(level));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.populateIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	public boolean isEnabledWhileUnderConstruction() {
		return this.enabledWhileUnderConstruction;
	}

	public boolean isEnabledWhileUpgrading() {
		return this.enabledWhileUpgrading;
	}

	public void addJassOrder(final CAbilityOrderButtonJass orderButton) {
		this.orderButtons.add(orderButton);
	}

	public void removeJassOrder(final CAbilityOrderButtonJass orderButton) {
		this.orderButtons.remove(orderButton);
	}

	public List<CAbilityOrderButtonJass> getOrderButtons() {
		return this.orderButtons;
	}

	public void setPhysical(final boolean physical) {
		this.physical = physical;
	}

	public void setUniversal(final boolean universal) {
		this.universal = universal;
	}

	public void setEnabledWhileUnderConstruction(final boolean enabledWhileUnderConstruction) {
		this.enabledWhileUnderConstruction = enabledWhileUnderConstruction;
	}

	public void setEnabledWhileUpgrading(final boolean enabledWhileUpgrading) {
		this.enabledWhileUpgrading = enabledWhileUpgrading;
	}

	@Override
	public War3ID getCode() {
		if (this.code == null) {
			return super.getCode();
		}
		return this.code;
	}

	public void setCode(final War3ID code) {
		this.code = code;
	}

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit unit) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) this.globalScope.parseType("unit"), unit));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.onSetUnitTypeIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	@Override
	protected void onSetDisabled(final boolean disabled, final CAbilityDisableType type) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		arguments.add(BooleanJassValue.of(disabled));
		// TODO avoid cast
		arguments.add(new HandleJassValue((HandleJassType) this.globalScope.parseType("abilitydisabletype"), type));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.setDisabledIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	@Override
	protected void onSetIconShowing(final boolean iconShowing) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		arguments.add(BooleanJassValue.of(iconShowing));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.setIconShowingIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}

	@Override
	protected void onSetPermanent(final boolean permanent) {
		final List<JassValue> arguments = new ArrayList<>();
		final StructJassValue structValue = getStructValue();
		arguments.add(structValue);
		arguments.add(BooleanJassValue.of(permanent));
		final Integer instructionPtr = structValue.getType().getMethodTable().get(this.setPermanentIdxVtable);
		this.globalScope.runThreadUntilCompletion(
				this.globalScope.createThread(instructionPtr, arguments, this.jassAbilityBasicScope));
	}
}
