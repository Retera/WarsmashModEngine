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
	private AbilityBuilderSpecialDisplayFields displayFields;
	private AbilityBuilderSpecialConfigFields specialFields;
	private AbilityBuilderOverrideFields overrideFields;

	private List<ABCondition> extraTargetConditions;
	private List<ABCondition> extraAutoTargetConditions;
	private List<ABCondition> extraAutoNoTargetConditions;
	private List<ABCondition> extraCastConditions;

	private List<ABAction> onAddAbility;
	private List<ABAction> onAddDisabledAbility;
	private List<ABAction> onRemoveAbility;
	private List<ABAction> onRemoveDisabledAbility;
	private List<ABAction> onDeathPreCast;
	private List<ABAction> onCancelPreCast;
	private List<ABAction> onOrderIssued;
	private List<ABAction> onActivate;
	private List<ABAction> onDeactivate;

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

	public AbilityBuilderSpecialDisplayFields getDisplayFields() {
		return displayFields;
	}

	public void setDisplayFields(AbilityBuilderSpecialDisplayFields displayFields) {
		this.displayFields = displayFields;
	}

	public AbilityBuilderSpecialConfigFields getSpecialFields() {
		return specialFields;
	}

	public void setSpecialFields(AbilityBuilderSpecialConfigFields specialFields) {
		this.specialFields = specialFields;
	}

	public AbilityBuilderOverrideFields getOverrideFields() {
		return overrideFields;
	}

	public void setOverrideFields(AbilityBuilderOverrideFields overrideFields) {
		this.overrideFields = overrideFields;
	}

	public List<ABCondition> getExtraTargetConditions() {
		return extraTargetConditions;
	}

	public void setExtraTargetConditions(List<ABCondition> extraTargetConditions) {
		this.extraTargetConditions = extraTargetConditions;
	}

	/**
	 * @return the extraAutoTargetConditions
	 */
	public List<ABCondition> getExtraAutoTargetConditions() {
		return extraAutoTargetConditions;
	}

	/**
	 * @param extraAutoTargetConditions the extraAutoTargetConditions to set
	 */
	public void setExtraAutoTargetConditions(List<ABCondition> extraAutoTargetConditions) {
		this.extraAutoTargetConditions = extraAutoTargetConditions;
	}

	public List<ABCondition> getExtraAutoNoTargetConditions() {
		return extraAutoNoTargetConditions;
	}

	public void setExtraAutoNoTargetConditions(List<ABCondition> extraAutoNoTargetConditions) {
		this.extraAutoNoTargetConditions = extraAutoNoTargetConditions;
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

	/**
	 * @return the onAddDisabledAbility
	 */
	public List<ABAction> getOnAddDisabledAbility() {
		return onAddDisabledAbility;
	}

	/**
	 * @param onAddDisabledAbility the onAddDisabledAbility to set
	 */
	public void setOnAddDisabledAbility(List<ABAction> onAddDisabledAbility) {
		this.onAddDisabledAbility = onAddDisabledAbility;
	}

	public List<ABAction> getOnRemoveAbility() {
		return onRemoveAbility;
	}

	public void setOnRemoveAbility(List<ABAction> onRemoveAbility) {
		this.onRemoveAbility = onRemoveAbility;
	}

	/**
	 * @return the onRemoveDisabledAbility
	 */
	public List<ABAction> getOnRemoveDisabledAbility() {
		return onRemoveDisabledAbility;
	}

	/**
	 * @param onRemoveDisabledAbility the onRemoveDisabledAbility to set
	 */
	public void setOnRemoveDisabledAbility(List<ABAction> onRemoveDisabledAbility) {
		this.onRemoveDisabledAbility = onRemoveDisabledAbility;
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

	public List<ABAction> getOnOrderIssued() {
		return onOrderIssued;
	}

	public void setOnOrderIssued(List<ABAction> onOrderIssued) {
		this.onOrderIssued = onOrderIssued;
	}

	public List<ABAction> getOnActivate() {
		return onActivate;
	}

	public void setOnActivate(List<ABAction> onActivate) {
		this.onActivate = onActivate;
	}

	public List<ABAction> getOnDeactivate() {
		return onDeactivate;
	}

	public void setOnDeactivate(List<ABAction> onDeactivate) {
		this.onDeactivate = onDeactivate;
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
