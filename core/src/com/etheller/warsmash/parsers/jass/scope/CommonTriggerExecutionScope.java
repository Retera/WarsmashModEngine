package com.etheller.warsmash.parsers.jass.scope;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialog;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialogButton;

public class CommonTriggerExecutionScope extends TriggerExecutionScope {
	private CUnit triggeringUnit;
	private CUnit filterUnit;
	private CUnit enumUnit;
	private CDestructable filterDestructable;
	private CDestructable enumDestructable;
	private CItem filterItem;
	private CItem enumItem;
	private CPlayerJass filterPlayer;
	private CPlayerJass enumPlayer;
	private CTimerJass expiringTimer;
	private CUnit enteringUnit;
	private CUnit leavingUnit;
	private CRegion triggeringRegion;
	private CPlayerJass triggeringPlayer;
	private CUnit levelingUnit;
	private CUnit learningUnit;
	private int learnedSkill;
	private int learnedSkillLevel;
	private CUnit revivableUnit;
	private CUnit revivingUnit;
	private CUnit attacker;
	private CUnit rescuer;
	private CUnit dyingUnit;
	private CUnit killingUnit;
	private CUnit decayingUnit;
	private CUnit constructingStructure;
	private CUnit cancelledStructure;
	private CUnit constructedStructure;
	private CUnit researchingUnit;
	private int researched;
	private int trainedUnitType;
	private CUnit trainedUnit;
	private CUnit detectedUnit;
	private CUnit summoningUnit;
	private CUnit summonedUnit;
	private CUnit transportUnit;
	private CUnit loadedUnit;
	private CUnit sellingUnit;
	private CUnit soldUnit;
	private CUnit buyingUnit;
	private CUnit soldItem;
	private CUnit changingUnit;
	private CPlayerJass changingUnitPrevOwner;
	private CUnit manipulatingUnit;
	private CItem manipulatedItem;
	private CUnit orderedUnit;
	private int issuedOrderId;
	private float orderPointX;
	private float orderPointY;
	private CWidget orderTarget;
	private CDestructable orderTargetDestructable;
	private CItem orderTargetItem;
	private CUnit orderTargetUnit;
	private CWidget triggerWidget;
	private CScriptDialog clickedDialog;
	private CScriptDialogButton clickedButton;
	private CUnit spellAbilityUnit;
	private CUnit spellTargetUnit;
	private AbilityPointTarget spellTargetPoint;
	private War3ID spellAbilityId;
	private JassGameEventsWar3 triggerEventId;

	public CommonTriggerExecutionScope(final Trigger triggeringTrigger) {
		super(triggeringTrigger);
	}

	public CommonTriggerExecutionScope(final Trigger triggeringTrigger, final TriggerExecutionScope parentScope) {
		super(triggeringTrigger);
		if (parentScope instanceof CommonTriggerExecutionScope) {
			copyFrom((CommonTriggerExecutionScope) parentScope);
		}
	}

	public CommonTriggerExecutionScope(final CommonTriggerExecutionScope parentScope) {
		super(parentScope.getTriggeringTrigger());
		copyFrom(parentScope);
	}

	private void copyFrom(final CommonTriggerExecutionScope parentScope) {
		this.triggeringUnit = parentScope.triggeringUnit;
		this.filterUnit = parentScope.filterUnit;
		this.enumUnit = parentScope.enumUnit;
		this.filterDestructable = parentScope.filterDestructable;
		this.enumDestructable = parentScope.enumDestructable;
		this.filterItem = parentScope.filterItem;
		this.enumItem = parentScope.enumItem;
		this.filterPlayer = parentScope.filterPlayer;
		this.enumPlayer = parentScope.enumPlayer;
		this.expiringTimer = parentScope.expiringTimer;
		this.enteringUnit = parentScope.enteringUnit;
		this.leavingUnit = parentScope.leavingUnit;
		this.triggeringRegion = parentScope.triggeringRegion;
		this.triggeringPlayer = parentScope.triggeringPlayer;
		this.levelingUnit = parentScope.levelingUnit;
		this.learningUnit = parentScope.learningUnit;
		this.learnedSkill = parentScope.learnedSkill;
		this.learnedSkillLevel = parentScope.learnedSkillLevel;
		this.revivableUnit = parentScope.revivableUnit;
		this.attacker = parentScope.attacker;
		this.rescuer = parentScope.rescuer;
		this.dyingUnit = parentScope.dyingUnit;
		this.killingUnit = parentScope.killingUnit;
		this.decayingUnit = parentScope.decayingUnit;
		this.constructingStructure = parentScope.constructingStructure;
		this.cancelledStructure = parentScope.cancelledStructure;
		this.constructedStructure = parentScope.constructedStructure;
		this.researchingUnit = parentScope.researchingUnit;
		this.researched = parentScope.researched;
		this.trainedUnitType = parentScope.trainedUnitType;
		this.trainedUnit = parentScope.trainedUnit;
		this.detectedUnit = parentScope.detectedUnit;
		this.summoningUnit = parentScope.summoningUnit;
		this.summonedUnit = parentScope.summonedUnit;
		this.transportUnit = parentScope.transportUnit;
		this.loadedUnit = parentScope.loadedUnit;
		this.sellingUnit = parentScope.sellingUnit;
		this.soldUnit = parentScope.soldUnit;
		this.buyingUnit = parentScope.buyingUnit;
		this.soldItem = parentScope.soldItem;
		this.changingUnit = parentScope.changingUnit;
		this.changingUnitPrevOwner = parentScope.changingUnitPrevOwner;
		this.manipulatingUnit = parentScope.manipulatingUnit;
		this.manipulatedItem = parentScope.manipulatedItem;
		this.orderedUnit = parentScope.orderedUnit;
		this.issuedOrderId = parentScope.issuedOrderId;
		this.orderPointX = parentScope.orderPointX;
		this.orderPointY = parentScope.orderPointY;
		this.orderTarget = parentScope.orderTarget;
		this.orderTargetDestructable = parentScope.orderTargetDestructable;
		this.orderTargetItem = parentScope.orderTargetItem;
		this.orderTargetUnit = parentScope.orderTargetUnit;
		this.triggerWidget = parentScope.triggerWidget;
		this.clickedDialog = parentScope.clickedDialog;
		this.clickedButton = parentScope.clickedButton;
		this.triggerEventId = parentScope.triggerEventId;
	}

	public CUnit getEnumUnit() {
		return this.enumUnit;
	}

	public CUnit getTriggeringUnit() {
		return this.triggeringUnit;
	}

	public CWidget getTriggerWidget() {
		return this.triggerWidget;
	}

	public CUnit getFilterUnit() {
		return this.filterUnit;
	}

	public CDestructable getFilterDestructable() {
		return this.filterDestructable;
	}

	public CDestructable getEnumDestructable() {
		return this.enumDestructable;
	}

	public CItem getFilterItem() {
		return this.filterItem;
	}

	public CItem getEnumItem() {
		return this.enumItem;
	}

	public CPlayerJass getFilterPlayer() {
		return this.filterPlayer;
	}

	public CPlayerJass getEnumPlayer() {
		return this.enumPlayer;
	}

	public CTimerJass getExpiringTimer() {
		return this.expiringTimer;
	}

	public CUnit getEnteringUnit() {
		return this.enteringUnit;
	}

	public CUnit getLeavingUnit() {
		return this.leavingUnit;
	}

	public CRegion getTriggeringRegion() {
		return this.triggeringRegion;
	}

	public CPlayerJass getTriggeringPlayer() {
		return this.triggeringPlayer;
	}

	public CUnit getLevelingUnit() {
		return this.levelingUnit;
	}

	public CUnit getLearningUnit() {
		return this.learningUnit;
	}

	public int getLearnedSkill() {
		return this.learnedSkill;
	}

	public int getLearnedSkillLevel() {
		return this.learnedSkillLevel;
	}

	public CUnit getRevivableUnit() {
		return this.revivableUnit;
	}

	public CUnit getRevivingUnit() {
		return this.revivingUnit;
	}

	public CUnit getAttacker() {
		return this.attacker;
	}

	public CUnit getRescuer() {
		return this.rescuer;
	}

	public CUnit getDyingUnit() {
		return this.dyingUnit;
	}

	public CUnit getKillingUnit() {
		return this.killingUnit;
	}

	public CUnit getDecayingUnit() {
		return this.decayingUnit;
	}

	public CUnit getConstructingStructure() {
		return this.constructingStructure;
	}

	public CUnit getCancelledStructure() {
		return this.cancelledStructure;
	}

	public CUnit getConstructedStructure() {
		return this.constructedStructure;
	}

	public CUnit getResearchingUnit() {
		return this.researchingUnit;
	}

	public int getResearched() {
		return this.researched;
	}

	public int getTrainedUnitType() {
		return this.trainedUnitType;
	}

	public CUnit getTrainedUnit() {
		return this.trainedUnit;
	}

	public CUnit getDetectedUnit() {
		return this.detectedUnit;
	}

	public CUnit getSummoningUnit() {
		return this.summoningUnit;
	}

	public CUnit getSummonedUnit() {
		return this.summonedUnit;
	}

	public CUnit getTransportUnit() {
		return this.transportUnit;
	}

	public CUnit getLoadedUnit() {
		return this.loadedUnit;
	}

	public CUnit getSellingUnit() {
		return this.sellingUnit;
	}

	public CUnit getSoldUnit() {
		return this.soldUnit;
	}

	public CUnit getBuyingUnit() {
		return this.buyingUnit;
	}

	public CUnit getSoldItem() {
		return this.soldItem;
	}

	public CUnit getChangingUnit() {
		return this.changingUnit;
	}

	public CPlayerJass getChangingUnitPrevOwner() {
		return this.changingUnitPrevOwner;
	}

	public CUnit getManipulatingUnit() {
		return this.manipulatingUnit;
	}

	public CItem getManipulatedItem() {
		return this.manipulatedItem;
	}

	public CUnit getOrderedUnit() {
		return this.orderedUnit;
	}

	public int getIssuedOrderId() {
		return this.issuedOrderId;
	}

	public float getOrderPointX() {
		return this.orderPointX;
	}

	public float getOrderPointY() {
		return this.orderPointY;
	}

	public CWidget getOrderTarget() {
		return this.orderTarget;
	}

	public CDestructable getOrderTargetDestructable() {
		return this.orderTargetDestructable;
	}

	public CItem getOrderTargetItem() {
		return this.orderTargetItem;
	}

	public CUnit getOrderTargetUnit() {
		return this.orderTargetUnit;
	}

	public CScriptDialogButton getClickedButton() {
		return this.clickedButton;
	}

	public CScriptDialog getClickedDialog() {
		return this.clickedDialog;
	}

	public JassGameEventsWar3 getTriggerEventId() {
		return this.triggerEventId;
	}

	public CUnit getSpellAbilityUnit() {
		return this.spellAbilityUnit;
	}

	public CUnit getSpellTargetUnit() {
		return this.spellTargetUnit;
	}

	public AbilityPointTarget getSpellTargetPoint() {
		return this.spellTargetPoint;
	}

	public War3ID getSpellAbilityId() {
		return this.spellAbilityId;
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CUnit filterUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterUnit = filterUnit;
		return scope;
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope, final CUnit enumUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumUnit = enumUnit;
		return scope;
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CItem filterItem) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterItem = filterItem;
		return scope;
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope, final CItem enumItem) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumItem = enumItem;
		return scope;
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CDestructable filterDestructable) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterDestructable = filterDestructable;
		return scope;
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope,
			final CDestructable enumDestructable) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumDestructable = enumDestructable;
		return scope;
	}

	public static CommonTriggerExecutionScope filterScope(final TriggerExecutionScope parentScope,
			final CPlayerJass filterPlayer) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.filterPlayer = filterPlayer;
		return scope;
	}

	public static CommonTriggerExecutionScope enumScope(final TriggerExecutionScope parentScope,
			final CPlayerJass enumPlayer) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(parentScope.getTriggeringTrigger(),
				parentScope);
		scope.enumPlayer = enumPlayer;
		return scope;
	}

	public static CommonTriggerExecutionScope expiringTimer(final Trigger trigger, final CTimerJass cTimerJass) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.expiringTimer = cTimerJass;
		return scope;
	}

	public static CommonTriggerExecutionScope unitEnterRegionScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final TriggerExecutionScope parentScope, final CUnit enteringUnit,
			final CRegion triggeringRegion) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, parentScope);
		scope.enteringUnit = enteringUnit;
		scope.triggeringUnit = enteringUnit;
		scope.triggeringRegion = triggeringRegion;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope unitLeaveRegionScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final TriggerExecutionScope parentScope, final CUnit leavingUnit,
			final CRegion triggeringRegion) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, parentScope);
		scope.leavingUnit = leavingUnit;
		scope.triggeringUnit = leavingUnit;
		scope.triggeringRegion = triggeringRegion;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope playerHeroLevelScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit hero) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggeringUnit = hero;
		scope.levelingUnit = hero;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope playerHeroRevivableScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit hero) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggeringUnit = hero;
		scope.revivableUnit = hero;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope unitDeathScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit dyingUnit, final CUnit killingUnit) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.dyingUnit = dyingUnit;
		scope.triggerWidget = dyingUnit;
		scope.triggeringUnit = dyingUnit;
		scope.killingUnit = killingUnit;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope widgetTriggerScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CWidget triggerWidget) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.triggerWidget = triggerWidget;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope triggerDialogScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CScriptDialog clickedDialog, final CScriptDialogButton clickedButton) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.clickedDialog = clickedDialog;
		scope.clickedButton = clickedButton;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope unitSpellEffectTargetScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit spellAbilityUnit, final CUnit targetUnit, final War3ID spellAbilityId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetUnit = targetUnit;
		scope.spellAbilityId = spellAbilityId;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static CommonTriggerExecutionScope unitSpellEffectPointScope(final JassGameEventsWar3 triggerEventId,
			final Trigger trigger, final CUnit spellAbilityUnit, final AbilityPointTarget targetPoint,
			final War3ID spellAbilityId) {
		final CommonTriggerExecutionScope scope = new CommonTriggerExecutionScope(trigger, TriggerExecutionScope.EMPTY);
		scope.spellAbilityUnit = spellAbilityUnit;
		scope.triggeringUnit = spellAbilityUnit;
		scope.spellTargetPoint = targetPoint;
		scope.spellAbilityId = spellAbilityId;
		scope.triggerEventId = triggerEventId;
		return scope;
	}

	public static interface UnitEventScopeBuilder {
		CommonTriggerExecutionScope create(JassGameEventsWar3 triggerEventId, Trigger trigger, CUnit unit);
	}

	public static interface WidgetEventScopeBuilder {
		CommonTriggerExecutionScope create(JassGameEventsWar3 triggerEventId, Trigger trigger, CWidget unit);
	}
}
