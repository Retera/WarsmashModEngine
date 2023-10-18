package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.definitions.impl.CAbilityTypeDefinitionAbilityBuilder;

public class AbilityBuilderConfiguration {
	private String id;
	
	private String castId;
	private String uncastId;
	private String autoCastOnId;
	private String autoCastOffId;

	private AbilityBuilderType type;
	private AbilityBuilderSpecialDisplayFields displayFields;
	private AbilityBuilderSpecialConfigFields specialFields;

	private List<ABCondition> extraTargetConditions;
	private List<ABCondition> extraCastConditions;
	
	private List<ABAction> onAddAbility;
	private List<ABAction> onRemoveAbility;
	private List<ABAction> onDeathPreCast;
	private List<ABAction> onCancelPreCast;
	private List<ABAction> onBeginCast;		//could become onOrderIssued? Not used currently
	private List<ABAction> onActivate;
	private List<ABAction> onDeactivate;
	
	private List<ABAction> onLevelChange;

	private List<ABAction> onBeginCasting;
	private List<ABAction> onEndCasting;
	private List<ABAction> onChannelTick;
	private List<ABAction> onEndChannel;

	public AbilityBuilderConfiguration(AbilityBuilderParser parser, AbilityBuilderDupe dupe) {
		this.id = dupe.getId();
		this.castId = dupe.getCastId();
		this.uncastId = dupe.getUncastId();
		this.autoCastOnId = dupe.getAutoCastOnId();
		this.autoCastOffId = dupe.getAutoCastOffId();
		this.type = parser.getType();
		this.displayFields = parser.getDisplayFields();
		this.specialFields = parser.getSpecialFields();
		
		this.extraTargetConditions = parser.getExtraTargetConditions();
		this.extraCastConditions = parser.getExtraCastConditions();
		
		this.onAddAbility = parser.getOnAddAbility();
		this.onRemoveAbility = parser.getOnRemoveAbility();
		this.onDeathPreCast = parser.getOnDeathPreCast();
		this.onCancelPreCast = parser.getOnCancelPreCast();
		this.onBeginCast = parser.getOnBeginCast();
		this.onActivate = parser.getOnActivate();
		this.onDeactivate = parser.getOnDeactivate();
		
		this.onLevelChange = parser.getOnLevelChange();

		this.onBeginCasting = parser.getOnBeginCasting();
		this.onEndCasting = parser.getOnEndCasting();
		this.onChannelTick = parser.getOnChannelTick();
		this.onEndChannel = parser.getOnEndChannel();
	}
	
	public CAbilityTypeDefinitionAbilityBuilder createDefinition() {
		return new CAbilityTypeDefinitionAbilityBuilder(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCastId() {
		return castId;
	}

	public void setCastId(String castId) {
		this.castId = castId;
	}

	public String getUncastId() {
		return uncastId;
	}

	public void setUncastId(String uncastId) {
		this.uncastId = uncastId;
	}

	public String getAutoCastOnId() {
		return autoCastOnId;
	}

	public void setAutoCastOnId(String autoCastOnId) {
		this.autoCastOnId = autoCastOnId;
	}

	public String getAutoCastOffId() {
		return autoCastOffId;
	}

	public void setAutoCastOffId(String autoCastOffId) {
		this.autoCastOffId = autoCastOffId;
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

	public List<ABAction> getOnLevelChange() {
		return onLevelChange;
	}

	public void setOnLevelChange(List<ABAction> onLevelChange) {
		this.onLevelChange = onLevelChange;
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

}
