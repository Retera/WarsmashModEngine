package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.MeleeRangeTargetOverride;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.StatBuffFromDataField;

public class AbilityBuilderParserTemplateFields {
	//Template only
	private AbilityBuilderTemplateType templateType;
	//Aura template
	private List<ABAction> addToAuraActions;
	private List<ABAction> updateAuraLevelActions;
	private List<ABAction> removeFromAuraActions;
	//Simple Aura Template
	private Map<Integer,List<War3ID>> abilityIdsToAddPerLevel;
	private List<War3ID> levellingAbilityIdsToAdd;
	//StatList
	private List<StatBuffFromDataField> statBuffsFromDataFields;
	private MeleeRangeTargetOverride meleeRangeTargetOverride;

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

	public List<StatBuffFromDataField> getStatBuffsFromDataFields() {
		return statBuffsFromDataFields;
	}

	public void setStatBuffsFromDataFields(List<StatBuffFromDataField> statBuffsFromDataFields) {
		this.statBuffsFromDataFields = statBuffsFromDataFields;
	}

	public MeleeRangeTargetOverride getMeleeRangeTargetOverride() {
		return meleeRangeTargetOverride;
	}

	public void setMeleeRangeTargetOverride(MeleeRangeTargetOverride meleeRangeTargetOverride) {
		this.meleeRangeTargetOverride = meleeRangeTargetOverride;
	}
}
