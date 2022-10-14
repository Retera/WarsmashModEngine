package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityTypeJassDefinition extends AbstractCAbilityTypeDefinition<CAbilityTypeLevelData>
		implements CAbilityTypeDefinition {
	private JassFunction onAddJass;
	private JassFunction onRemoveJass;
	private JassFunction onTickJass;
	private JassFunction onDeathJass;
	private JassFunction onCancelFromQueueJass;
	private BehaviorExpr beginJass;
	private TriggerBooleanExpression checkBeforeQueueJass;
	private TriggerBooleanExpression checkTargetJass;
	private TriggerBooleanExpression checkUseJass;

	private final List<JassOrder> jassOrders;

	private final GlobalScope jassGlobalScope;

	private boolean enabledWhileUnderConstruction;
	private boolean enabledWhileUpgrading;

	private War3ID code;

	public CAbilityTypeJassDefinition(final GlobalScope jassGlobalScope) {
		this.jassGlobalScope = jassGlobalScope;
		this.jassOrders = new ArrayList<>();
	}

	public void setCode(final War3ID code) {
		this.code = code;
	}

	@Override
	protected CAbilityTypeLevelData createLevelData(final MutableGameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeLevelData(targetsAllowedAtLevel);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeLevelData> levelData) {
		return new CAbilityTypeJass(alias, this.code, levelData, this.jassGlobalScope, this);
	}

	public void setOnAddJass(final JassFunction onAddJass) {
		this.onAddJass = onAddJass;
	}

	public void setOnRemoveJass(final JassFunction onRemoveJass) {
		this.onRemoveJass = onRemoveJass;
	}

	public void setOnTickJass(final JassFunction onTickJass) {
		this.onTickJass = onTickJass;
	}

	public void setOnDeathJass(final JassFunction onDeathJass) {
		this.onDeathJass = onDeathJass;
	}

	public void setOnCancelFromQueueJass(final JassFunction onCancelFromQueueJass) {
		this.onCancelFromQueueJass = onCancelFromQueueJass;
	}

	public void setBeginJass(final BehaviorExpr beginJass) {
		this.beginJass = beginJass;
	}

	public void setCheckBeforeQueueJass(final TriggerBooleanExpression checkBeforeQueueJass) {
		this.checkBeforeQueueJass = checkBeforeQueueJass;
	}

	public void setCheckTargetCondition(final TriggerBooleanExpression checkTargetJass) {
		this.checkTargetJass = checkTargetJass;
	}

	public void setCheckUseCondition(final TriggerBooleanExpression checkUseJass) {
		this.checkUseJass = checkUseJass;
	}

	public boolean isEnabledWhileUnderConstruction() {
		return this.enabledWhileUnderConstruction;
	}

	public void setEnabledWhileUnderConstruction(final boolean enabledWhileUnderConstruction) {
		this.enabledWhileUnderConstruction = enabledWhileUnderConstruction;
	}

	public boolean isEnabledWhileUpgrading() {
		return this.enabledWhileUpgrading;
	}

	public void setEnabledWhileUpgrading(final boolean enabledWhileUpgrading) {
		this.enabledWhileUpgrading = enabledWhileUpgrading;
	}

	public void addJassOrder(final JassOrder order) {
		this.jassOrders.add(order);
	}

	public void removeJassOrder(final JassOrder order) {
		this.jassOrders.remove(order);
	}

	public List<JassOrder> getJassOrders() {
		return this.jassOrders;
	}

	private void execute(final JassFunction function, final List<JassValue> args, final TriggerExecutionScope scope) {
		if (function != null) {
			function.call(args, this.jassGlobalScope, scope);
		}
	}

	private JassValue evaluate(final JassFunction function, final List<JassValue> args,
			final TriggerExecutionScope scope) {
		if (function != null) {
			return function.call(args, this.jassGlobalScope, scope);
		}
		return null;
	}

	private CBehavior evaluateBehavior(final JassFunction function, final CSimulation game, final CUnit caster,
			final List<JassValue> args, final TriggerExecutionScope scope) {
		final JassValue userBehaviorValue = evaluate(function, args, scope);
		if (userBehaviorValue == null) {
			return caster.pollNextOrderBehavior(game);
		}
		final CBehavior userBehavior = userBehaviorValue.visit(ObjectJassValueVisitor.getInstance());
		return userBehavior;
	}

	private boolean evaluateBoolean(final JassFunction function, final CSimulation game, final CUnit caster,
			final List<JassValue> args, final TriggerExecutionScope scope) {
		final JassValue userBooleanValue = evaluate(function, args, scope);
		if (userBooleanValue == null) {
			return false;
		}
		return userBooleanValue.visit(BooleanJassValueVisitor.getInstance());
	}

	public void onAdd(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit) {
		execute(this.onAddJass, Collections.emptyList(), abilityJass.getJassAbilityBasicScope());
	}

	public void onRemove(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit) {
		execute(this.onRemoveJass, Collections.emptyList(), abilityJass.getJassAbilityBasicScope());
	}

	public void onTick(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit) {
		execute(this.onTickJass, Collections.emptyList(), abilityJass.getJassAbilityBasicScope());
	}

	public void onDeath(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit) {
		execute(this.onDeathJass, Collections.emptyList(), abilityJass.getJassAbilityBasicScope());
	}

	public void onCancelFromQueue(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit,
			final int orderId) {
		execute(this.onCancelFromQueueJass, Collections.emptyList(),
				CommonTriggerExecutionScope.jassAbilityBasicScope(abilityJass, unit, abilityJass.getAlias(), orderId));
	}

	public CBehavior begin(final CSimulation game, final CAbilityJass abilityJass, final CUnit caster,
			final int orderId, final CWidget target) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, caster, orderId);
		if (orderCommandCardIcon.type == JassOrderButtonType.INSTANT_NO_TARGET) {
			if (this.beginJass != null) {
				CommonTriggerExecutionScope scope;
				scope = target.visit(new CWidgetVisitor<CommonTriggerExecutionScope>() {
					@Override
					public CommonTriggerExecutionScope accept(final CUnit target) {
						return CommonTriggerExecutionScope.jassAbilityTargetScope(abilityJass, caster, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon);
					}

					@Override
					public CommonTriggerExecutionScope accept(final CDestructable target) {
						return CommonTriggerExecutionScope.jassAbilityTargetScope(abilityJass, caster, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon);
					}

					@Override
					public CommonTriggerExecutionScope accept(final CItem target) {
						return CommonTriggerExecutionScope.jassAbilityTargetScope(abilityJass, caster, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon);
					}
				});
				return this.beginJass.evaluate(this.jassGlobalScope, scope);
			}
			else {
				return caster.pollNextOrderBehavior(game);
			}
		}
		else {
			return caster.pollNextOrderBehavior(game);
		}
	}

	public CBehavior begin(final CSimulation game, final CAbilityJass abilityJass, final CUnit caster,
			final int orderId, final AbilityPointTarget point) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, caster, orderId);
		if (orderCommandCardIcon.type == JassOrderButtonType.INSTANT_NO_TARGET) {
			if (this.beginJass != null) {
				return this.beginJass.evaluate(this.jassGlobalScope,
						CommonTriggerExecutionScope.jassAbilityPointScope(abilityJass, caster, point,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon));
			}
			else {
				return caster.pollNextOrderBehavior(game);
			}
		}
		else {
			return caster.pollNextOrderBehavior(game);
		}
	}

	public CBehavior beginNoTarget(final CSimulation game, final CAbilityJass abilityJass, final CUnit caster,
			final int orderId) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, caster, orderId);
		if (orderCommandCardIcon.type == JassOrderButtonType.INSTANT_NO_TARGET) {
			if (this.beginJass != null) {
				return this.beginJass.evaluate(this.jassGlobalScope,
						CommonTriggerExecutionScope.jassAbilityNoTargetScope(abilityJass, caster,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon));
			}
			else {
				return caster.pollNextOrderBehavior(game);
			}
		}
		else {
			return caster.pollNextOrderBehavior(game);
		}
	}

	public boolean checkBeforeQueue(final CSimulation game, final CAbilityJass abilityJass, final CUnit caster,
			final int orderId, final AbilityTarget target) {
		if (orderId != 0) {
			for (final JassOrder order : this.jassOrders) {
				if (order.autoCastOrderId == orderId) {
					order.autoCastActive = true;
					return false;
				}
				else if (order.autoCastUnOrderId == orderId) {
					order.autoCastActive = false;
					return false;
				}
			}
		}
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, caster, orderId);
		if (orderCommandCardIcon.type == JassOrderButtonType.INSTANT_NO_TARGET_NO_INTERRUPT) {
			if (this.checkBeforeQueueJass != null) {
				return this.checkBeforeQueueJass.evaluate(this.jassGlobalScope,
						CommonTriggerExecutionScope.jassAbilityNoTargetScope(abilityJass, caster,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon));
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}

	public void checkCanTarget(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit,
			final int orderId, final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, unit, orderId);
		if ((orderCommandCardIcon != null) && ((orderCommandCardIcon.type == JassOrderButtonType.UNIT_TARGET)
				|| (orderCommandCardIcon.type == JassOrderButtonType.UNIT_OR_POINT_TARGET))) {
			if (this.checkTargetJass != null) {
				CommonTriggerExecutionScope scope;
				scope = target.visit(new CWidgetVisitor<CommonTriggerExecutionScope>() {
					@Override
					public CommonTriggerExecutionScope accept(final CUnit target) {
						return CommonTriggerExecutionScope.jassAbilityTargetScope(abilityJass, unit, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon);
					}

					@Override
					public CommonTriggerExecutionScope accept(final CDestructable target) {
						return CommonTriggerExecutionScope.jassAbilityTargetScope(abilityJass, unit, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon);
					}

					@Override
					public CommonTriggerExecutionScope accept(final CItem target) {
						return CommonTriggerExecutionScope.jassAbilityTargetScope(abilityJass, unit, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon);
					}
				});
				if (this.checkTargetJass.evaluate(this.jassGlobalScope, scope)) {
					receiver.targetOk(target);
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				receiver.targetOk(target);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	public void checkCanTarget(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit,
			final int orderId, final AbilityPointTarget target,
			final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, unit, orderId);
		if ((orderCommandCardIcon != null) && ((orderCommandCardIcon.type == JassOrderButtonType.POINT_TARGET)
				|| (orderCommandCardIcon.type == JassOrderButtonType.UNIT_OR_POINT_TARGET))) {
			if (this.checkTargetJass != null) {
				if (this.checkTargetJass.evaluate(this.jassGlobalScope,
						CommonTriggerExecutionScope.jassAbilityPointScope(abilityJass, unit, target,
								abilityJass.getAlias(), orderId, orderCommandCardIcon.type, orderCommandCardIcon))) {
					receiver.targetOk(target);
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				receiver.targetOk(target);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	public void checkCanTargetNoTarget(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit,
			final int orderId, final AbilityTargetCheckReceiver<Void> receiver) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, unit, orderId);
		if ((orderCommandCardIcon != null) && (orderCommandCardIcon.type == JassOrderButtonType.INSTANT_NO_TARGET)) {
			if (this.checkTargetJass != null) {
				if (this.checkTargetJass.evaluate(this.jassGlobalScope,
						CommonTriggerExecutionScope.jassAbilityNoTargetScope(abilityJass, unit, abilityJass.getAlias(),
								orderId, orderCommandCardIcon.type, orderCommandCardIcon))) {
					receiver.targetOk(null);
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				receiver.targetOk(null);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	public void checkCanUse(final CSimulation game, final CAbilityJass abilityJass, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final JassOrder orderCommandCardIcon = getOrderCommandCardIcon(game, unit, orderId);
		if ((orderCommandCardIcon == null) || orderCommandCardIcon.disabled) {
			receiver.disabled();
		}
		else if (orderCommandCardIcon.type == JassOrderButtonType.PASSIVE) {
			receiver.notAnActiveAbility();
		}
		else if (orderCommandCardIcon.charges == 0) {
			receiver.noChargesRemaining();
		}
		else if (unit.getMana() < orderCommandCardIcon.manaCost) {
			receiver.notEnoughResources(ResourceType.MANA);
		}
		else {
			final CPlayer player = game.getPlayer(unit.getPlayerIndex());
			if (player.getGold() < orderCommandCardIcon.goldCost) {
				receiver.notEnoughResources(ResourceType.GOLD);
			}
			else if (player.getLumber() < orderCommandCardIcon.lumberCost) {
				receiver.notEnoughResources(ResourceType.LUMBER);
			}
			else if ((player.getFoodUsed() + orderCommandCardIcon.foodCostDisplayOnly) < player.getFoodCap()) {
				receiver.notEnoughResources(ResourceType.FOOD);
			}
			else {
				if (this.checkUseJass != null) {
					if (this.checkUseJass.evaluate(this.jassGlobalScope,
							CommonTriggerExecutionScope.jassAbilityBasicScope(abilityJass, unit, abilityJass.getAlias(),
									orderId, orderCommandCardIcon.type, orderCommandCardIcon))) {
						receiver.useOk();
					}
					else {
						receiver.unknownReasonUseNotOk();
					}
				}
				else {
					receiver.useOk();
				}
			}
		}
	}

	public JassOrder getOrderCommandCardIcon(final CSimulation game, final CUnit unit, final int orderId) {
		JassOrder commandCardIconToUse = null;
		// TODO for now this is trying to disambiguate between multiple icons with the
		// same ID if you have
		// them. I try to put forward a best guess, but that's generally a broken
		// concept and maybe later
		// this function should just be simplified to return based on orderID (such as
		// if we just used
		// a simple map lookup). Two icons on same ability with same order ID should not
		// be allowed.
		for (final JassOrder orderCommandCardIcon : this.jassOrders) {
			if (orderCommandCardIcon.orderId == orderId) {
				if (commandCardIconToUse == null) {
					commandCardIconToUse = orderCommandCardIcon;
				}
				if (orderCommandCardIcon.disabled) {
					continue;
				}
				if (orderCommandCardIcon.type == JassOrderButtonType.PASSIVE) {
					continue;
				}
				if (orderCommandCardIcon.charges == 0) {
					continue;
				}
				if (unit.getMana() < orderCommandCardIcon.manaCost) {
					continue;
				}
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				if (player.getGold() < orderCommandCardIcon.goldCost) {
					continue;
				}
				if (player.getLumber() < orderCommandCardIcon.lumberCost) {
					continue;
				}
				if ((player.getFoodUsed() + orderCommandCardIcon.foodCostDisplayOnly) < player.getFoodCap()) {
					continue;
				}
				commandCardIconToUse = orderCommandCardIcon;
			}
		}
		return commandCardIconToUse;
	}

	public static final class JassOrder {
		private static final int INFINITE_CHARGES = -1;

		private int orderId;
		private int autoCastOrderId;
		private int autoCastUnOrderId;
		private int containerMenuOrderId;
		private boolean disabled; // TODO instance
		private int manaCost;
		private int goldCost;
		private int lumberCost;
		private int charges = INFINITE_CHARGES; // TODO instance
		private int foodCostDisplayOnly;
		private JassOrderButtonType type;
		private boolean autoCastActive; // TODO instance

		// UI
		private boolean hidden; // TODO instance
		private String iconPath;
		private int buttonPositionX;
		private int buttonPositionY;
		private String tip;
		private String uberTip;
		private char hotkey;

		private String mouseTargetModelPath;
		private String mouseTargetPathingMap;
		private War3ID previewBuildUnitId;
		private float mouseTargetRadius;

		public JassOrder(final int orderId, final int buttonPositionX, final int buttonPositionY) {
			this.orderId = orderId;
			this.buttonPositionX = buttonPositionX;
			this.buttonPositionY = buttonPositionY;
		}

		public int getOrderId() {
			return this.orderId;
		}

		public void setOrderId(final int orderId) {
			this.orderId = orderId;
		}

		public int getAutoCastOrderId() {
			return this.autoCastOrderId;
		}

		public void setAutoCastOrderId(final int autoCastOrderId) {
			this.autoCastOrderId = autoCastOrderId;
		}

		public int getAutoCastUnOrderId() {
			return this.autoCastUnOrderId;
		}

		public void setAutoCastUnOrderId(final int autoCastUnOrderId) {
			this.autoCastUnOrderId = autoCastUnOrderId;
		}

		public int getContainerMenuOrderId() {
			return this.containerMenuOrderId;
		}

		public void setContainerMenuOrderId(final int containerMenuOrderId) {
			this.containerMenuOrderId = containerMenuOrderId;
		}

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(final boolean disabled) {
			this.disabled = disabled;
		}

		public int getManaCost() {
			return this.manaCost;
		}

		public void setManaCost(final int manaCost) {
			this.manaCost = manaCost;
		}

		public int getGoldCost() {
			return this.goldCost;
		}

		public void setGoldCost(final int goldCost) {
			this.goldCost = goldCost;
		}

		public int getLumberCost() {
			return this.lumberCost;
		}

		public void setLumberCost(final int lumberCost) {
			this.lumberCost = lumberCost;
		}

		public int getCharges() {
			return this.charges;
		}

		public void setCharges(final int charges) {
			this.charges = charges;
		}

		public int getFoodCostDisplayOnly() {
			return this.foodCostDisplayOnly;
		}

		public void setFoodCostDisplayOnly(final int foodCostDisplayOnly) {
			this.foodCostDisplayOnly = foodCostDisplayOnly;
		}

		public JassOrderButtonType getType() {
			return this.type;
		}

		public void setType(final JassOrderButtonType type) {
			this.type = type;
		}

		public boolean isAutoCastActive() {
			return this.autoCastActive;
		}

		public void setAutoCastActive(final boolean autoCastActive) {
			this.autoCastActive = autoCastActive;
		}

		public boolean isHidden() {
			return this.hidden;
		}

		public void setHidden(final boolean hidden) {
			this.hidden = hidden;
		}

		public String getIconPath() {
			return this.iconPath;
		}

		public void setIconPath(final String iconPath) {
			this.iconPath = iconPath;
		}

		public int getButtonPositionX() {
			return this.buttonPositionX;
		}

		public void setButtonPositionX(final int buttonPositionX) {
			this.buttonPositionX = buttonPositionX;
		}

		public int getButtonPositionY() {
			return this.buttonPositionY;
		}

		public void setButtonPositionY(final int buttonPositionY) {
			this.buttonPositionY = buttonPositionY;
		}

		public String getTip() {
			return this.tip;
		}

		public void setTip(final String tip) {
			this.tip = tip;
		}

		public String getUberTip() {
			return this.uberTip;
		}

		public void setUberTip(final String uberTip) {
			this.uberTip = uberTip;
		}

		public char getHotkey() {
			return this.hotkey;
		}

		public void setHotkey(final char hotkey) {
			this.hotkey = hotkey;
		}

		public String getMouseTargetModelPath() {
			return this.mouseTargetModelPath;
		}

		public void setMouseTargetModelPath(final String mouseTargetModelPath) {
			this.mouseTargetModelPath = mouseTargetModelPath;
		}

		public String getMouseTargetPathingMap() {
			return this.mouseTargetPathingMap;
		}

		public void setMouseTargetPathingMap(final String mouseTargetPathingMap) {
			this.mouseTargetPathingMap = mouseTargetPathingMap;
		}

		public War3ID getPreviewBuildUnitId() {
			return this.previewBuildUnitId;
		}

		public void setPreviewBuildUnitId(final War3ID previewBuildUnitId) {
			this.previewBuildUnitId = previewBuildUnitId;
		}

		public float getMouseTargetRadius() {
			return this.mouseTargetRadius;
		}

		public void setMouseTargetRadius(final float mouseTargetRadius) {
			this.mouseTargetRadius = mouseTargetRadius;
		}
	}

	public static enum JassOrderButtonType {
		INSTANT_NO_TARGET, UNIT_TARGET, POINT_TARGET, UNIT_OR_POINT_TARGET, INSTANT_NO_TARGET_NO_INTERRUPT, PASSIVE,
		MENU;

		public static JassOrderButtonType[] VALUES = values();
	}
}
