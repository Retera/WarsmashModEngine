package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.timer.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitgroup.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalActions.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalCallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.fxcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.listenercallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.timercallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitgroupcallbacks.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.ability.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.comparison.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.numeric.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unit.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbilityBuilderGsonBuilder {

	private static void registerAbilityCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackGetStoredAbilityByKey.class, "getStoredAbilityByKey")
				.registerSubtype(ABCallbackGetLastCreatedAbility.class, "getLastCreatedAbility");
	}

	private static void registerAttackPostDamageListenerCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedAttackPostDamageListener.class,
						"getLastCreatedAttackPostDamageListener")
				.registerSubtype(ABCallbackGetStoredAttackPostDamageListenerByKey.class,
						"getStoredAttackPostDamageListenerByKey");
	}

	private static void registerAttackPreDamageListenerCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedAttackPreDamageListener.class,
						"getLastCreatedAttackPreDamageListener")
				.registerSubtype(ABCallbackGetStoredAttackPreDamageListenerByKey.class,
						"getStoredAttackPreDamageListenerByKey");
	}

	private static void registerBooleanCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackRawBoolean.class, "rawBoolean")
				.registerSubtype(ABCallbackGetAbilityDataAsBoolean.class, "getAbilityDataAsBoolean")
				.registerSubtype(ABCallbackGetParentAbilityDataAsBoolean.class, "getParentAbilityDataAsBoolean")
				.registerSubtype(ABCallbackIsTriggeringDamageAnAttack.class, "isTriggeringDamageAnAttack")
				.registerSubtype(ABCallbackIsTriggeringDamageRanged.class, "isTriggeringDamageRanged");
	}

	private static void registerBuffCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackGetStoredBuffByKey.class, "getStoredBuffByKey")
				.registerSubtype(ABCallbackGetLastCreatedBuff.class, "getLastCreatedBuff");
	}

	private static void registerDamageTakenListenerCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedDamageTakenListener.class, "getLastCreatedDamageTakenListener")
				.registerSubtype(ABCallbackGetStoredDamageTakenListenerByKey.class,
						"getStoredDamageTakenListenerByKey");
	}

	private static void registerDamageTakenModificationListenerCallbacks(
			RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedDamageTakenModificationListener.class,
						"getLastCreatedDamageTakenModificationListener")
				.registerSubtype(ABCallbackGetStoredDamageTakenModificationListenerByKey.class,
						"getStoredDamageTakenModificationListenerByKey");
	}

	private static void registerDeathReplacementEffectCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedDeathReplacement.class, "getLastCreatedDeathReplacement")
				.registerSubtype(ABCallbackGetStoredDeathReplacementByKey.class, "getStoredDeathReplacementByKey");
	}

	private static void registerEvasionListenerCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedEvasionListener.class, "getLastCreatedEvasionListener")
				.registerSubtype(ABCallbackGetStoredEvasionListenerByKey.class, "getStoredEvasionListenerByKey");
	}

	private static void registerFinalDamageTakenModificationListenerCallbacks(
			RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedFinalDamageTakenModificationListener.class,
						"getLastCreatedFinalDamageTakenModificationListener")
				.registerSubtype(ABCallbackGetStoredFinalDamageTakenModificationListenerByKey.class,
						"getStoredFinalDamageTakenModificationListenerByKey");
	}

	private static void registerFloatCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackGetAbilityArea.class, "getAbilityArea")
				.registerSubtype(ABCallbackGetAbilityDuration.class, "getAbilityDuration")
				.registerSubtype(ABCallbackGetAbilityHeroDuration.class, "getAbilityHeroDuration")
				.registerSubtype(ABCallbackRawFloat.class, "rawFloat")
				.registerSubtype(ABCallbackIntToFloat.class, "intToFloat")
				.registerSubtype(ABCallbackNegativeFloat.class, "negativeFloat")
				.registerSubtype(ABCallbackPi.class, "pi").registerSubtype(ABCallbackCos.class, "cos")
				.registerSubtype(ABCallbackSin.class, "sin")
				.registerSubtype(ABCallbackGetStoredFloatByKey.class, "getStoredFloatByKey")
				.registerSubtype(ABCallbackGetAbilityDataAsFloat.class, "getAbilityDataAsFloat")
				.registerSubtype(ABCallbackGetParentAbilityDataAsFloat.class, "getParentAbilityDataAsFloat")
				.registerSubtype(ABCallbackRandomFloat.class, "randomFloat")
				.registerSubtype(ABCallbackRandomBoundedFloat.class, "randomBoundedFloat")
				.registerSubtype(ABCallbackGetUnitLocationX.class, "getUnitLocationX")
				.registerSubtype(ABCallbackGetUnitLocationY.class, "getUnitLocationY")
				.registerSubtype(ABCallbackGetLocationX.class, "getLocationX")
				.registerSubtype(ABCallbackGetLocationY.class, "getLocationY")
				.registerSubtype(ABCallbackGetUnitFacing.class, "getUnitFacing")
				.registerSubtype(ABCallbackGetUnitCurrentMana.class, "getUnitCurrentMana")

				.registerSubtype(ABCallbackGetTotalDamageDealt.class, "getTotalDamageDealt");
	}

	private static void registerGenericFloatCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackAddFloat.class, "addFloat")
				.registerSubtype(ABCallbackSubtractFloat.class, "subtractFloat")
				.registerSubtype(ABCallbackMultiplyFloat.class, "multiplyFloat")
				.registerSubtype(ABCallbackDivideFloat.class, "divideFloat")
				.registerSubtype(ABCallbackMinFloat.class, "minFloat")
				.registerSubtype(ABCallbackMaxFloat.class, "maxFloat");
	}

	private static void registerSpecialFloatCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackAddFloat.class, "+")
				.registerSubtype(ABCallbackSubtractFloat.class, "-").registerSubtype(ABCallbackMultiplyFloat.class, "*")
				.registerSubtype(ABCallbackDivideFloat.class, "/")
				.registerSubtype(ABCallbackMinFloat.class, "min").registerSubtype(ABCallbackMaxFloat.class, "max");
	}

	private static void registerFxCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackGetLastCreatedSpellEffect.class, "getLastCreatedSpellEffect")
				.registerSubtype(ABCallbackGetStoredFXByKey.class, "getStoredFXByKey");
	}

	private static void registerIdCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackGetStoredIDByKey.class, "getStoredIDByKey")
				.registerSubtype(ABCallbackGetWar3IDFromString.class, "getWar3IDFromString")
				.registerSubtype(ABCallbackGetAlias.class, "getAlias")
				.registerSubtype(ABCallbackGetParentAlias.class, "getParentAlias")
				.registerSubtype(ABCallbackGetFirstBuffId.class, "getFirstBuffId")
				.registerSubtype(ABCallbackGetFirstEffectId.class, "getFirstEffectId");
	}

	private static void registerIntegerCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackRawInteger.class, "rawInteger")
				.registerSubtype(ABCallbackGetStoredIntegerByKey.class, "getStoredIntegerByKey")
				.registerSubtype(ABCallbackCountUnitsInRangeOfUnit.class, "countUnitsInRangeOfUnit")
				.registerSubtype(ABCallbackCountUnitsInRangeOfLocation.class, "countUnitsInRangeOfLocation")
				.registerSubtype(ABCallbackGetSpellLevel.class, "getSpellLevel");
	}

	private static void registerLocationCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackCreateLocationFromXY.class, "createLocationFromXY")
				.registerSubtype(ABCallbackGetStoredLocationByKey.class, "getStoredLocationByKey")
				.registerSubtype(ABCallbackGetTargetedLocation.class, "getTargetedLocation");
	}

	private static void registerStatBuffCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory
				.registerSubtype(ABCallbackGetLastCreatedNonStackingStatBuff.class, "getLastCreatedNonStackingStatBuff")
				.registerSubtype(ABCallbackGetStoredNonStackingStatBuffByKey.class,
						"getStoredNonStackingStatBuffByKey");
	}

	private static void registerStringCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackRawString.class, "rawString")
				.registerSubtype(ABCallbackCatStrings.class, "catStrings")
				.registerSubtype(ABCallbackGetUnitHandleAsString.class, "getUnitHandleAsString")
				.registerSubtype(ABCallbackGetAllowStackingKey.class, "getAllowStackingKey");
	}

	private static void registerTimerCallbacks(RuntimeTypeAdapterFactory callbackTypeFactory) {
		callbackTypeFactory.registerSubtype(ABCallbackGetLastCreatedTimer.class, "getLastCreatedTimer")
				.registerSubtype(ABCallbackGetLastStartedTimer.class, "getLastStartedTimer")
				.registerSubtype(ABCallbackGetFiringTimer.class, "getFiringTimer")
				.registerSubtype(ABCallbackGetStoredTimerByKey.class, "getStoredTimerByKey");
	}

	private static void registerUnitCallbacks(RuntimeTypeAdapterFactory unitCallbackTypeFactory) {
		unitCallbackTypeFactory.registerSubtype(ABCallbackGetCastingUnit.class, "getCastingUnit")
				.registerSubtype(ABCallbackGetEnumUnit.class, "getEnumUnit")
				.registerSubtype(ABCallbackGetAttackedUnit.class, "getAttackedUnit")
				.registerSubtype(ABCallbackGetAttackingUnit.class, "getAttackingUnit")
				.registerSubtype(ABCallbackGetDyingUnit.class, "getDyingUnit")
				.registerSubtype(ABCallbackGetKillingUnit.class, "getKillingUnit")
				.registerSubtype(ABCallbackGetAbilityTargetedUnit.class, "getAbilityTargetedUnit")
				.registerSubtype(ABCallbackGetStoredUnitByKey.class, "getStoredUnitByKey")
				.registerSubtype(ABCallbackGetParentCastingUnit.class, "getParentCastingUnit");
	}

	private static void registerUnitGroupCallbacks(RuntimeTypeAdapterFactory unitGroupCallbackTypeFactory) {
		unitGroupCallbackTypeFactory.registerSubtype(ABCallbackGetUnitGroupByName.class, "getUnitGroupByName")
				.registerSubtype(ABCallbackGetLastCreatedUnitGroup.class, "getLastCreatedUnitGroup");
	}

	private static void registerConditions(RuntimeTypeAdapterFactory<ABCondition> conditionTypeFactory) {
		conditionTypeFactory.registerSubtype(ABConditionAnd.class, "and").registerSubtype(ABConditionOr.class, "or")
				.registerSubtype(ABConditionNot.class, "not").registerSubtype(ABConditionBool.class, "bool")
				.registerSubtype(ABConditionFloatEqual.class, "floatEq")
				.registerSubtype(ABConditionFloatGte.class, "floatGte")
				.registerSubtype(ABConditionFloatGt.class, "floatGt")
				.registerSubtype(ABConditionFloatLte.class, "floatLte")
				.registerSubtype(ABConditionFloatLt.class, "floatLt")
				.registerSubtype(ABConditionFloatNe.class, "floatNe")
				.registerSubtype(ABConditionFloatNe0.class, "floatNe0")
				.registerSubtype(ABConditionIntegerEqual.class, "integerEqual")
				.registerSubtype(ABConditionIsValidTarget.class, "isValidTarget")
				.registerSubtype(ABConditionIsUnitInRangeOfUnit.class, "isUnitInRangeOfUnit")
				.registerSubtype(ABConditionIsUnitInGroup.class, "isUnitInGroup")
				.registerSubtype(ABConditionIsUnitEqual.class, "isUnitEqual")
				.registerSubtype(ABConditionIsAttackTypeEqual.class, "isAttackTypeEqual")
				.registerSubtype(ABConditionIsDamageTypeEqual.class, "isDamageTypeEqual")

				.registerSubtype(ABConditionIsOnCooldown.class, "isOnCooldown")

				.registerSubtype(ABConditionIsUnitBuilding.class, "isUnitBuilding");
	}

	private static void registerActions(RuntimeTypeAdapterFactory<ABAction> factory) {
		factory.registerSubtype(ABActionIf.class, "if")
				.registerSubtype(ABActionIterateUnitsInRangeOfUnit.class, "iterateUnitsInRangeOfUnit")
				.registerSubtype(ABActionIterateUnitsInRangeOfLocation.class, "iterateUnitsInRangeOfLocation")
				.registerSubtype(ABActionIterateUnitsInRect.class, "iterateUnitsInRect")
				.registerSubtype(ABActionPeriodicExecute.class, "periodicExecute")
				.registerSubtype(ABActionIterateActions.class, "iterateActions")

				.registerSubtype(ABActionCreateSpellEffectOnUnit.class, "createSpellEffectOnUnit")
				.registerSubtype(ABActionCreateTemporarySpellEffectOnUnit.class, "createTemporarySpellEffectOnUnit")
				.registerSubtype(ABActionCreateSpellEffectAtLocation.class, "createSpellEffectAtLocation")
				.registerSubtype(ABActionCreateTemporarySpellEffectAtLocation.class,
						"createTemporarySpellEffectAtLocation")
				.registerSubtype(ABActionCreateSpellEffectAtPoint.class, "createSpellEffectAtPoint")
				.registerSubtype(ABActionCreateTemporarySpellEffectAtPoint.class, "createTemporarySpellEffectAtPoint")

				.registerSubtype(ABActionAddAbility.class, "addAbility")
				.registerSubtype(ABActionRemoveAbility.class, "removeAbility")
				.registerSubtype(ABActionStoreValueLocally.class, "storeValueLocally")
				.registerSubtype(ABActionRemoveSpellEffect.class, "removeSpellEffect")
				.registerSubtype(ABActionCreateAbilityFromId.class, "createAbilityFromId")
				.registerSubtype(ABActionCreateBuffFromId.class, "createBuffFromId")
				.registerSubtype(ABActionAddDefenseBonus.class, "addDefenseBonus")
				.registerSubtype(ABActionRemoveDefenseBonus.class, "removeDefenseBonus")
				
				.registerSubtype(ABActionCreateUnitGroup.class, "createUnitGroup")
				.registerSubtype(ABActionIterateUnitsInGroup.class, "iterateUnitsInGroup")
				.registerSubtype(ABActionAddUnitToGroup.class, "addUnitToGroup")
				.registerSubtype(ABActionRemoveUnitFromGroup.class, "removeUnitFromGroup")
				.registerSubtype(ABActionDamageTarget.class, "damageTarget").registerSubtype(ABActionHeal.class, "heal")
				.registerSubtype(ABActionSetHp.class, "setHp").registerSubtype(ABActionResurrect.class, "resurrect")
				.registerSubtype(ABActionSetMp.class, "setMp")
				.registerSubtype(ABActionSubtractMp.class, "subtractMp")
				.registerSubtype(ABActionAddStunBuff.class, "addStunBuff")
				.registerSubtype(ABActionStartCooldown.class, "startCooldown")
				.registerSubtype(ABActionDeactivateToggledAbility.class, "deactivateToggledAbility")

				.registerSubtype(ABActionCreateTimer.class, "createTimer")
				.registerSubtype(ABActionStartTimer.class, "startTimer")
				.registerSubtype(ABActionRemoveTimer.class, "removeTimer")

				.registerSubtype(ABActionAddBuff.class, "addBuff")
				.registerSubtype(ABActionAddNonStackingDisplayBuff.class, "addNonStackingDisplayBuff")
				.registerSubtype(ABActionRemoveBuff.class, "removeBuff")
				.registerSubtype(ABActionCreatePassiveBuff.class, "createPassiveBuff")
				.registerSubtype(ABActionCreateTimedBuff.class, "createTimedBuff")
				.registerSubtype(ABActionCreateTimedArtBuff.class, "createTimedArtBuff")
				.registerSubtype(ABActionCreateTimedTickingBuff.class, "createTimedTickingBuff")
				.registerSubtype(ABActionCreateTimedTickingPostDeathBuff.class, "createTimedTickingPostDeathBuff")

				.registerSubtype(ABActionCreateNonStackingStatBuff.class, "createNonStackingStatBuff")
				.registerSubtype(ABActionAddNonStackingStatBuff.class, "addNonStackingStatBuff")
				.registerSubtype(ABActionRemoveNonStackingStatBuff.class, "removeNonStackingStatBuff")
				.registerSubtype(ABActionUpdateNonStackingStatBuff.class, "updateNonStackingStatBuff")
				.registerSubtype(ABActionRecomputeStatBuffsOnUnit.class, "recomputeStatBuffsOnUnit")

				.registerSubtype(ABActionCreateAttackPostDamageListener.class, "createAttackPostDamageListener")
				.registerSubtype(ABActionAddAttackPostDamageListener.class, "addAttackPostDamageListener")
				.registerSubtype(ABActionRemoveAttackPostDamageListener.class, "removeAttackPostDamageListener")
				.registerSubtype(ABActionCreateAttackPreDamageListener.class, "createAttackPreDamageListener")
				.registerSubtype(ABActionAddAttackPreDamageListener.class, "addAttackPreDamageListener")
				.registerSubtype(ABActionRemoveAttackPreDamageListener.class, "removeAttackPreDamageListener")
				.registerSubtype(ABActionCreateDamageTakenListener.class, "createDamageTakenListener")
				.registerSubtype(ABActionAddDamageTakenListener.class, "addDamageTakenListener")
				.registerSubtype(ABActionRemoveDamageTakenListener.class, "removeDamageTakenListener")
				.registerSubtype(ABActionCreateDamageTakenModificationListener.class,
						"createDamageTakenModificationListener")
				.registerSubtype(ABActionAddDamageTakenModificationListener.class, "addDamageTakenModificationListener")
				.registerSubtype(ABActionRemoveDamageTakenModificationListener.class,
						"removeDamageTakenModificationListener")
				.registerSubtype(ABActionCreateFinalDamageTakenModificationListener.class,
						"createFinalDamageTakenModificationListener")
				.registerSubtype(ABActionAddFinalDamageTakenModificationListener.class, "addFinalDamageTakenModificationListener")
				.registerSubtype(ABActionRemoveFinalDamageTakenModificationListener.class,
						"removeFinalDamageTakenModificationListener")
				.registerSubtype(ABActionCreateEvasionListener.class, "createEvasionListener")
				.registerSubtype(ABActionAddEvasionListener.class, "addEvasionListener")
				.registerSubtype(ABActionRemoveEvasionListener.class, "removeEvasionListener")
				.registerSubtype(ABActionCreateDeathReplacementEffect.class, "createDeathReplacementEffect")
				.registerSubtype(ABActionAddDeathReplacementEffect.class, "addDeathReplacementEffect")
				.registerSubtype(ABActionRemoveDeathReplacementEffect.class, "removeDeathReplacementEffect")

				.registerSubtype(ABActionDamageTakenModificationSetDamageMultiplier.class, "setDamageTakenMultiplier")
				.registerSubtype(ABActionDamageTakenModificationMultiplyDamageMultiplier.class,
						"multiplyDamageTakenMultiplier")
				.registerSubtype(ABActionPreDamageListenerAddDamageMultiplier.class, "addDamageDealtMultiplier")
				.registerSubtype(ABActionPreDamageListenerAddBonusDamage.class, "addBonusDamageDealt")
				.registerSubtype(ABActionSetPreDamageStacking.class, "setStacking")
				.registerSubtype(ABActionDeathReplacementSetReviving.class, "setReviving")
				.registerSubtype(ABActionDeathReplacementSetReincarnating.class, "setReincarnating")
				.registerSubtype(ABActionDeathReplacementFinishReincarnating.class, "finishReincarnating")
				.registerSubtype(ABActionSubtractTotalDamageDealt.class, "subtractTotalDamageDealt");
	}

	public static Gson create() {
		GsonBuilder gsonBuilder = new GsonBuilder();

		final RuntimeTypeAdapterFactory<ABAction> actionTypeFactory = RuntimeTypeAdapterFactory.of(ABAction.class,
				"type");
		registerActions(actionTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(actionTypeFactory);

		final RuntimeTypeAdapterFactory<ABCallback> callbackTypeFactory = RuntimeTypeAdapterFactory.of(ABCallback.class,
				"type");
		registerAbilityCallbacks(callbackTypeFactory);
		registerAttackPostDamageListenerCallbacks(callbackTypeFactory);
		registerAttackPreDamageListenerCallbacks(callbackTypeFactory);
		registerBooleanCallbacks(callbackTypeFactory);
		registerBuffCallbacks(callbackTypeFactory);
		registerDamageTakenListenerCallbacks(callbackTypeFactory);
		registerDamageTakenModificationListenerCallbacks(callbackTypeFactory);
		registerDeathReplacementEffectCallbacks(callbackTypeFactory);
		registerEvasionListenerCallbacks(callbackTypeFactory);
		registerFinalDamageTakenModificationListenerCallbacks(callbackTypeFactory);
		registerFloatCallbacks(callbackTypeFactory);
		registerGenericFloatCallbacks(callbackTypeFactory);
		registerFxCallbacks(callbackTypeFactory);
		registerIdCallbacks(callbackTypeFactory);
		registerIntegerCallbacks(callbackTypeFactory);
		registerLocationCallbacks(callbackTypeFactory);
		registerStatBuffCallbacks(callbackTypeFactory);
		registerStringCallbacks(callbackTypeFactory);
		registerTimerCallbacks(callbackTypeFactory);
		registerUnitCallbacks(callbackTypeFactory);
		registerUnitGroupCallbacks(callbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(callbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABAbilityCallback> abilityCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABAbilityCallback.class, "type");
		registerAbilityCallbacks(abilityCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(abilityCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABAttackPostDamageListenerCallback> attackPostDamageListenerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABAttackPostDamageListenerCallback.class, "type");
		registerAttackPostDamageListenerCallbacks(attackPostDamageListenerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(attackPostDamageListenerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABAttackPreDamageListenerCallback> attackPreDamageListenerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABAttackPreDamageListenerCallback.class, "type");
		registerAttackPreDamageListenerCallbacks(attackPreDamageListenerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(attackPreDamageListenerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABBooleanCallback> booleanCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABBooleanCallback.class, "type");
		registerBooleanCallbacks(booleanCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(booleanCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABBuffCallback> buffCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABBuffCallback.class, "type");
		registerBuffCallbacks(buffCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(buffCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABDamageTakenListenerCallback> damageTakenListenerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABDamageTakenListenerCallback.class, "type");
		registerDamageTakenListenerCallbacks(damageTakenListenerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(damageTakenListenerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABDamageTakenModificationListenerCallback> damageTakenModificationListenerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABDamageTakenModificationListenerCallback.class, "type");
		registerDamageTakenModificationListenerCallbacks(damageTakenModificationListenerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(damageTakenModificationListenerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABDeathReplacementCallback> deathReplacementEffectCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABDeathReplacementCallback.class, "type");
		registerDeathReplacementEffectCallbacks(deathReplacementEffectCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(deathReplacementEffectCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABEvasionListenerCallback> evasionListenerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABEvasionListenerCallback.class, "type");
		registerEvasionListenerCallbacks(evasionListenerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(evasionListenerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABFinalDamageTakenModificationListenerCallback> finalDamageTakenModificationListenerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABFinalDamageTakenModificationListenerCallback.class, "type");
		registerFinalDamageTakenModificationListenerCallbacks(finalDamageTakenModificationListenerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(finalDamageTakenModificationListenerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABFloatCallback> floatCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABFloatCallback.class, "type");
		registerFloatCallbacks(floatCallbackTypeFactory);
		registerSpecialFloatCallbacks(floatCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(floatCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABFXCallback> fxCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABFXCallback.class, "type");
		registerFxCallbacks(fxCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(fxCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABIDCallback> idCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABIDCallback.class, "type");
		registerIdCallbacks(idCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(idCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABIntegerCallback> integerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABIntegerCallback.class, "type");
		registerIntegerCallbacks(integerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(integerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABLocationCallback> locationCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABLocationCallback.class, "type");
		registerLocationCallbacks(locationCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(locationCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABNonStackingStatBuffCallback> statBuffCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABNonStackingStatBuffCallback.class, "type");
		registerStatBuffCallbacks(statBuffCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(statBuffCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABStringCallback> stringCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABStringCallback.class, "type");
		registerStringCallbacks(stringCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(stringCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABTimerCallback> timerCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABTimerCallback.class, "type");
		registerTimerCallbacks(timerCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(timerCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABUnitCallback> unitCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABUnitCallback.class, "type");
		registerUnitCallbacks(unitCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(unitCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABUnitGroupCallback> unitGroupCallbackTypeFactory = RuntimeTypeAdapterFactory
				.of(ABUnitGroupCallback.class, "type");
		registerUnitGroupCallbacks(unitGroupCallbackTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(unitGroupCallbackTypeFactory);

		final RuntimeTypeAdapterFactory<ABCondition> conditionTypeFactory = RuntimeTypeAdapterFactory
				.of(ABCondition.class, "type");
		registerConditions(conditionTypeFactory);
		gsonBuilder.registerTypeAdapterFactory(conditionTypeFactory);

		// ENUM CALLBACKS
		final RuntimeTypeAdapterFactory<ABNonStackingStatBuffTypeCallback> nssbtTypeFactory = RuntimeTypeAdapterFactory
				.of(ABNonStackingStatBuffTypeCallback.class, "type");
		nssbtTypeFactory.registerSubtype(ABCallbackGetNonStackingStatBuffTypeFromString.class,
				"getNonStackingStatBuffTypeFromString");
		gsonBuilder.registerTypeAdapterFactory(nssbtTypeFactory);

		final RuntimeTypeAdapterFactory<ABAttackTypeCallback> atTypeFactory = RuntimeTypeAdapterFactory
				.of(ABAttackTypeCallback.class, "type");
		atTypeFactory.registerSubtype(ABCallbackGetAttackTypeFromString.class, "getAttackTypeFromString")
				.registerSubtype(ABCallbackGetTriggeringAttackType.class, "getTriggeringAttackType");
		gsonBuilder.registerTypeAdapterFactory(atTypeFactory);

		final RuntimeTypeAdapterFactory<ABDamageTypeCallback> dtTypeFactory = RuntimeTypeAdapterFactory
				.of(ABDamageTypeCallback.class, "type");
		dtTypeFactory.registerSubtype(ABCallbackGetDamageTypeFromString.class, "getDamageTypeFromString")
				.registerSubtype(ABCallbackGetTriggeringDamageType.class, "getTriggeringDamageType");
		gsonBuilder.registerTypeAdapterFactory(dtTypeFactory);

		final RuntimeTypeAdapterFactory<ABAttackPreDamageListenerPriorityCallback> pdlpTypeFactory = RuntimeTypeAdapterFactory
				.of(ABAttackPreDamageListenerPriorityCallback.class, "type");
		pdlpTypeFactory.registerSubtype(ABCallbackRawPreDamageListenerPriority.class, "rawPriority");
		gsonBuilder.registerTypeAdapterFactory(pdlpTypeFactory);

		final RuntimeTypeAdapterFactory<ABDeathReplacementPriorityCallback> drepTypeFactory = RuntimeTypeAdapterFactory
				.of(ABDeathReplacementPriorityCallback.class, "type");
		drepTypeFactory.registerSubtype(ABCallbackRawDeathEffectPriority.class, "rawPriority");
		gsonBuilder.registerTypeAdapterFactory(drepTypeFactory);

		return gsonBuilder.create();
	}
}
