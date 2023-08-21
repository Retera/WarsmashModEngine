package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.MeleeRangeTargetOverride;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.StatBuffFromDataField;

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

	private List<ABAction> onLevelChange;

	private List<ABAction> onBeginCasting;
	private List<ABAction> onEndCasting;
	private List<ABAction> onChannelTick;
	private List<ABAction> onEndChannel;
	
	//Template only
	private AbilityBuilderParserTemplateFields templateFields;

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

	public List<ABAction> getOnLevelChange() {
		return onLevelChange;
	}

	public void setOnLevelChange(List<ABAction> onLevelChange) {
		this.onLevelChange = onLevelChange;
	}

	public List<ABAction> getOnEndCasting() {
		return onEndCasting;
	}

	public void setOnEndCasting(List<ABAction> onEndCasting) {
		this.onEndCasting = onEndCasting;
	}

	public List<ABAction> getOnChannelTick() {
		return onChannelTick;
	}

	public void setOnChannelTick(List<ABAction> onChannelTick) {
		this.onChannelTick = onChannelTick;
	}

	public List<ABAction> getOnEndChannel() {
		return onEndChannel;
	}

	public void setOnEndChannel(List<ABAction> onEndChannel) {
		this.onEndChannel = onEndChannel;
	}

	public AbilityBuilderTemplateType getTemplateType() {
		return templateFields.getTemplateType();
	}

	public void setTemplateType(AbilityBuilderTemplateType templateType) {
		this.templateFields.setTemplateType(templateType);
	}

	public List<ABAction> getAddToAuraActions() {
		return templateFields.getAddToAuraActions();
	}

	public void setAddToAuraActions(List<ABAction> addToAuraActions) {
		this.templateFields.setAddToAuraActions(addToAuraActions);
	}

	public List<ABAction> getUpdateAuraLevelActions() {
		return templateFields.getUpdateAuraLevelActions();
	}

	public void setUpdateAuraLevelActions(List<ABAction> updateAuraLevelActions) {
		this.templateFields.setUpdateAuraLevelActions(updateAuraLevelActions);
	}

	public List<ABAction> getRemoveFromAuraActions() {
		return templateFields.getRemoveFromAuraActions();
	}

	public void setRemoveFromAuraActions(List<ABAction> removeFromAuraActions) {
		this.templateFields.setRemoveFromAuraActions(removeFromAuraActions);
	}

	public Map<Integer, List<War3ID>> getAbilityIdsToAddPerLevel() {
		return templateFields.getAbilityIdsToAddPerLevel();
	}

	public void setAbilityIdsToAddPerLevel(Map<Integer, List<War3ID>> abilityIdsToAddPerLevel) {
		this.setAbilityIdsToAddPerLevel(abilityIdsToAddPerLevel);
	}

	public List<War3ID> getLevellingAbilityIdsToAdd() {
		return templateFields.getLevellingAbilityIdsToAdd();
	}

	public void setLevellingAbilityIdsToAdd(List<War3ID> levellingAbilityIdsToAdd) {
		this.templateFields.setLevellingAbilityIdsToAdd(levellingAbilityIdsToAdd);
	}

	public List<StatBuffFromDataField> getStatBuffsFromDataFields() {
		return templateFields.getStatBuffsFromDataFields();
	}

	public void setStatBuffsFromDataFields(List<StatBuffFromDataField> statBuffsFromDataFields) {
		this.templateFields.setStatBuffsFromDataFields(statBuffsFromDataFields);
	}

	public MeleeRangeTargetOverride getMeleeRangeTargetOverride() {
		return templateFields.getMeleeRangeTargetOverride();
	}

	public void setMeleeRangeTargetOverride(MeleeRangeTargetOverride meleeRangeTargetOverride) {
		this.templateFields.setMeleeRangeTargetOverride(meleeRangeTargetOverride);
	}
}
