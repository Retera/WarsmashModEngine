package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields.ABAbilityBuilderOverrideFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields.ABAbilityBuilderSpecialConfigFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields.ABAbilityBuilderSpecialDisplayFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.definitions.impl.ABAbilityBuilderTypeDefinition;

public class ABAbilityBuilderConfiguration {
	private String id;
	
	private String castId;
	private String uncastId;
	private String autoCastOnId;
	private String autoCastOffId;
	private AutocastType autoCastType;

	private ABAbilityBuilderType type;
	private ABAbilityBuilderSpecialDisplayFields displayFields;
	private ABAbilityBuilderSpecialConfigFields specialFields;
	private ABAbilityBuilderOverrideFields overrideFields;

	private List<ABBooleanCallback> extraTargetConditions;
	private List<ABBooleanCallback> extraAutoTargetConditions;
	private List<ABBooleanCallback> extraAutoNoTargetConditions;
	private List<ABBooleanCallback> extraCastConditions;
	
	private List<ABAction> onAddAbility;
	private List<ABAction> onAddDisabledAbility;
	private List<ABAction> onRemoveAbility;
	private List<ABAction> onRemoveDisabledAbility;
	
	private List<ABAction> onDeathPreCast;
	private List<ABAction> onCancelPreCast;
	private List<ABAction> onOrderIssued;
	private List<ABAction> onActivate;
	private List<ABAction> onDeactivate;
	private List<ABAction> onChangeAutoCast;
	
	private List<ABAction> onLevelChange;

	private List<ABAction> onBeginCasting;
	private List<ABAction> onEndCasting;
	private List<ABAction> onChannelTick;
	private List<ABAction> onEndChannel;
	
	private Map<String, List<ABAction>> reuseActions;
	private Map<String, ABCallback> reuseCallbacks;

	private List<ABStringCallback> initialUniqueFlags;

	public ABAbilityBuilderConfiguration(ABAbilityBuilderParser parser, ABAbilityBuilderDupe dupe) {
		this.id = dupe.getId();
		this.castId = dupe.getCastId();
		this.uncastId = dupe.getUncastId();
		this.autoCastOnId = dupe.getAutoCastOnId();
		this.autoCastOffId = dupe.getAutoCastOffId();
		this.autoCastType = dupe.getAutoCastType();
		this.type = parser.getType();
		this.displayFields = parser.getDisplayFields();
		this.specialFields = parser.getSpecialFields();
		this.overrideFields = parser.getOverrideFields();
		
		this.extraTargetConditions = parser.getExtraTargetConditions();
		this.extraAutoTargetConditions = parser.getExtraAutoTargetConditions();
		this.extraCastConditions = parser.getExtraCastConditions();
		this.extraAutoNoTargetConditions = parser.getExtraAutoNoTargetConditions();
		
		this.onAddAbility = parser.getOnAddAbility();
		this.onAddDisabledAbility = parser.getOnAddDisabledAbility();
		this.onRemoveAbility = parser.getOnRemoveAbility();
		this.onRemoveDisabledAbility = parser.getOnRemoveDisabledAbility();
		this.onDeathPreCast = parser.getOnDeathPreCast();
		this.onCancelPreCast = parser.getOnCancelPreCast();
		this.onOrderIssued = parser.getOnOrderIssued();
		this.onActivate = parser.getOnActivate();
		this.onDeactivate = parser.getOnDeactivate();
		this.onChangeAutoCast = parser.getOnChangeAutoCast();
		
		this.onLevelChange = parser.getOnLevelChange();

		this.onBeginCasting = parser.getOnBeginCasting();
		this.onEndCasting = parser.getOnEndCasting();
		this.onChannelTick = parser.getOnChannelTick();
		this.onEndChannel = parser.getOnEndChannel();
		
		this.setReuseActions(parser.getReuseActions());
		this.setReuseCallbacks(parser.getReuseCallbacks());
		
		this.setInitialUniqueFlags(parser.getInitialUniqueFlags());
	}
	
	public ABAbilityBuilderTypeDefinition createDefinition() {
		return new ABAbilityBuilderTypeDefinition(this);
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

	/**
	 * @return the autoCastType
	 */
	public AutocastType getAutoCastType() {
		return autoCastType;
	}

	/**
	 * @param autoCastType the autoCastType to set
	 */
	public void setAutoCastType(AutocastType autoCastType) {
		this.autoCastType = autoCastType;
	}

	public ABAbilityBuilderType getType() {
		return type;
	}

	public void setType(ABAbilityBuilderType type) {
		this.type = type;
	}

	public ABAbilityBuilderSpecialDisplayFields getDisplayFields() {
		return displayFields;
	}

	public void setDisplayFields(ABAbilityBuilderSpecialDisplayFields displayFields) {
		this.displayFields = displayFields;
	}

	public ABAbilityBuilderSpecialConfigFields getSpecialFields() {
		return specialFields;
	}

	public void setSpecialFields(ABAbilityBuilderSpecialConfigFields specialFields) {
		this.specialFields = specialFields;
	}

	public ABAbilityBuilderOverrideFields getOverrideFields() {
		return overrideFields;
	}

	public void setOverrideFields(ABAbilityBuilderOverrideFields overrideFields) {
		this.overrideFields = overrideFields;
	}

	public List<ABBooleanCallback> getExtraTargetConditions() {
		return extraTargetConditions;
	}

	public void setExtraTargetConditions(List<ABBooleanCallback> extraTargetConditions) {
		this.extraTargetConditions = extraTargetConditions;
	}

	/**
	 * @return the extraAutoTargetConditions
	 */
	public List<ABBooleanCallback> getExtraAutoTargetConditions() {
		return extraAutoTargetConditions;
	}

	/**
	 * @param extraAutoTargetConditions the extraAutoTargetConditions to set
	 */
	public void setExtraAutoTargetConditions(List<ABBooleanCallback> extraAutoTargetConditions) {
		this.extraAutoTargetConditions = extraAutoTargetConditions;
	}

	public List<ABBooleanCallback> getExtraAutoNoTargetConditions() {
		return extraAutoNoTargetConditions;
	}

	public void setExtraAutoNoTargetConditions(List<ABBooleanCallback> extraAutoNoTargetConditions) {
		this.extraAutoNoTargetConditions = extraAutoNoTargetConditions;
	}

	public List<ABBooleanCallback> getExtraCastConditions() {
		return extraCastConditions;
	}

	public void setExtraCastConditions(List<ABBooleanCallback> extraCastConditions) {
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

	public List<ABAction> getOnChangeAutoCast() {
		return onChangeAutoCast;
	}

	public void setOnChangeAutoCast(List<ABAction> onChangeAutoCast) {
		this.onChangeAutoCast = onChangeAutoCast;
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

	public Map<String, List<ABAction>> getReuseActions() {
		return reuseActions;
	}

	public void setReuseActions(Map<String, List<ABAction>> reuseActions) {
		this.reuseActions = reuseActions;
	}

	public Map<String, ABCallback> getReuseCallbacks() {
		return reuseCallbacks;
	}

	public void setReuseCallbacks(Map<String, ABCallback> reuseCallbacks) {
		this.reuseCallbacks = reuseCallbacks;
	}

	public List<ABStringCallback> getInitialUniqueFlags() {
		return this.initialUniqueFlags;
	}

	public void setInitialUniqueFlags( List<ABStringCallback> initialUniqueFlags) {
		this.initialUniqueFlags = initialUniqueFlags;
	}

}
