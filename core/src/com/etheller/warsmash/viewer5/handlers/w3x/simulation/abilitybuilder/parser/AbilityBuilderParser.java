package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class AbilityBuilderParser {

	private List<AbilityBuilderDupe> ids;

	private AbilityBuilderType type;
	private AbilityBuilderSpecialConfigFields specialFields;

	private List<ABCondition> extraTargetConditions;
	private List<ABCondition> extraCastConditions;
	
	private List<ABAction> onAddAbility;
	private List<ABAction> onRemoveAbility;
	private List<ABAction> onTickPreCast;
	private List<ABAction> onDeathPreCast;
	private List<ABAction> onCancelPreCast;
	private List<ABAction> onBeginCast;

	private List<ABAction> onBeginCasting;
	private List<ABAction> onEndCasting;
	private List<ABAction> onResetCasting;
	private List<ABAction> onUpdateCasting;
	
	//Template only
	private AbilityBuilderTemplateType templateType;
	//Aura template
	private List<ABAction> addToAuraActions;
	private List<ABAction> updateAuraLevelActions;
	private List<ABAction> removeFromAuraActions;

	public List<AbilityBuilderDupe> getIds() {
		return ids;
	}

	public void setIds(List<AbilityBuilderDupe> ids) {
		this.ids = ids;
	}

	public AbilityBuilderType getType() {
		return type;
	}

	public void setType(AbilityBuilderType type) {
		this.type = type;
	}

	public AbilityBuilderSpecialConfigFields getSpecialFields() {
		return specialFields;
	}

	public void setSpecialFields(AbilityBuilderSpecialConfigFields specialFields) {
		this.specialFields = specialFields;
	}

	public List<ABCondition> getExtraTargetConditions() {
		return extraTargetConditions;
	}

	public void setExtraTargetConditions(List<ABCondition> extraTargetConditions) {
		this.extraTargetConditions = extraTargetConditions;
	}

	public List<ABCondition> getExtraCastConditions() {
		return extraCastConditions;
	}

	public void setExtraCastConditions(List<ABCondition> extraCastConditions) {
		this.extraCastConditions = extraCastConditions;
	}

	public List<ABAction> getOnAddAbility() {
		return onAddAbility;
	}

	public void setOnAddAbility(List<ABAction> onAddAbility) {
		this.onAddAbility = onAddAbility;
	}

	public List<ABAction> getOnRemoveAbility() {
		return onRemoveAbility;
	}

	public void setOnRemoveAbility(List<ABAction> onRemoveAbility) {
		this.onRemoveAbility = onRemoveAbility;
	}

	public List<ABAction> getOnTickPreCast() {
		return onTickPreCast;
	}

	public void setOnTickPreCast(List<ABAction> onTickPreCast) {
		this.onTickPreCast = onTickPreCast;
	}

	public List<ABAction> getOnDeathPreCast() {
		return onDeathPreCast;
	}

	public void setOnDeathPreCast(List<ABAction> onDeathPreCast) {
		this.onDeathPreCast = onDeathPreCast;
	}

	public List<ABAction> getOnCancelPreCast() {
		return onCancelPreCast;
	}

	public void setOnCancelPreCast(List<ABAction> onCancelPreCast) {
		this.onCancelPreCast = onCancelPreCast;
	}

	public List<ABAction> getOnBeginCast() {
		return onBeginCast;
	}

	public void setOnBeginCast(List<ABAction> onBeginCast) {
		this.onBeginCast = onBeginCast;
	}

	public List<ABAction> getOnBeginCasting() {
		return onBeginCasting;
	}

	public void setOnBeginCasting(List<ABAction> onBeginCasting) {
		this.onBeginCasting = onBeginCasting;
	}

	public List<ABAction> getOnEndCasting() {
		return onEndCasting;
	}

	public void setOnEndCasting(List<ABAction> onEndCasting) {
		this.onEndCasting = onEndCasting;
	}

	public List<ABAction> getOnResetCasting() {
		return onResetCasting;
	}

	public void setOnResetCasting(List<ABAction> onResetCasting) {
		this.onResetCasting = onResetCasting;
	}

	public List<ABAction> getOnUpdateCasting() {
		return onUpdateCasting;
	}

	public void setOnUpdateCasting(List<ABAction> onUpdateCasting) {
		this.onUpdateCasting = onUpdateCasting;
	}

	public AbilityBuilderTemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(AbilityBuilderTemplateType templateType) {
		this.templateType = templateType;
	}

	public List<ABAction> getAddToAuraActions() {
		return addToAuraActions;
	}

	public void setAddToAuraActions(List<ABAction> addToAuraActions) {
		this.addToAuraActions = addToAuraActions;
	}

	public List<ABAction> getUpdateAuraLevelActions() {
		return updateAuraLevelActions;
	}

	public void setUpdateAuraLevelActions(List<ABAction> updateAuraLevelActions) {
		this.updateAuraLevelActions = updateAuraLevelActions;
	}

	public List<ABAction> getRemoveFromAuraActions() {
		return removeFromAuraActions;
	}

	public void setRemoveFromAuraActions(List<ABAction> removeFromAuraActions) {
		this.removeFromAuraActions = removeFromAuraActions;
	}
}
