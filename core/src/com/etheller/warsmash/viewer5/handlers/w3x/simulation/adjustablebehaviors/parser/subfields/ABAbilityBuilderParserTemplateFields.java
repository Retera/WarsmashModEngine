package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderTemplateType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template.ABMeleeRangeTargetOverride;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template.ABStatBuffFromDataField;

public class ABAbilityBuilderParserTemplateFields {
	//Template only
	private ABAbilityBuilderTemplateType templateType;
	//Aura template
	private List<ABAction> addToAuraActions;
	private List<ABAction> updateAuraLevelActions;
	private List<ABAction> removeFromAuraActions;
	//Simple Aura Template
	private Map<Integer,List<War3ID>> abilityIdsToAddPerLevel;
	private List<War3ID> levellingAbilityIdsToAdd;
	//StatList
	private List<ABStatBuffFromDataField> statBuffsFromDataFields;
	private ABMeleeRangeTargetOverride meleeRangeTargetOverride;

	public void updateFromParent(ABAbilityBuilderParserTemplateFields parent) {
		if (this.templateType == null)
			this.templateType = parent.templateType;

		if (this.addToAuraActions == null)
			this.addToAuraActions = parent.addToAuraActions;
		if (this.updateAuraLevelActions == null)
			this.updateAuraLevelActions = parent.updateAuraLevelActions;
		if (this.removeFromAuraActions == null)
			this.removeFromAuraActions = parent.removeFromAuraActions;

		if (this.abilityIdsToAddPerLevel == null)
			this.abilityIdsToAddPerLevel = parent.abilityIdsToAddPerLevel;
		if (this.levellingAbilityIdsToAdd == null)
			this.levellingAbilityIdsToAdd = parent.levellingAbilityIdsToAdd;

		if (this.statBuffsFromDataFields == null)
			this.statBuffsFromDataFields = parent.statBuffsFromDataFields;
		if (this.meleeRangeTargetOverride == null)
			this.meleeRangeTargetOverride = parent.meleeRangeTargetOverride;
	}

	public ABAbilityBuilderTemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(ABAbilityBuilderTemplateType templateType) {
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

	public Map<Integer, List<War3ID>> getAbilityIdsToAddPerLevel() {
		return abilityIdsToAddPerLevel;
	}

	public void setAbilityIdsToAddPerLevel(Map<Integer, List<War3ID>> abilityIdsToAddPerLevel) {
		this.abilityIdsToAddPerLevel = abilityIdsToAddPerLevel;
	}

	public List<War3ID> getLevellingAbilityIdsToAdd() {
		return levellingAbilityIdsToAdd;
	}

	public void setLevellingAbilityIdsToAdd(List<War3ID> levellingAbilityIdsToAdd) {
		this.levellingAbilityIdsToAdd = levellingAbilityIdsToAdd;
	}

	public List<ABStatBuffFromDataField> getStatBuffsFromDataFields() {
		return statBuffsFromDataFields;
	}

	public void setStatBuffsFromDataFields(List<ABStatBuffFromDataField> statBuffsFromDataFields) {
		this.statBuffsFromDataFields = statBuffsFromDataFields;
	}

	public ABMeleeRangeTargetOverride getMeleeRangeTargetOverride() {
		return meleeRangeTargetOverride;
	}

	public void setMeleeRangeTargetOverride(ABMeleeRangeTargetOverride meleeRangeTargetOverride) {
		this.meleeRangeTargetOverride = meleeRangeTargetOverride;
	}
}
