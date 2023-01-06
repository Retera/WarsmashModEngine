package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.definitions.impl.CAbilityTypeDefinitionAbilityBuilder;

public class AbilityBuilderParser {
	private String id;

	private AbilityBuilderType type;
	private boolean active;

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

	public CAbilityTypeDefinitionAbilityBuilder createDefinition() {
		return new CAbilityTypeDefinitionAbilityBuilder(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AbilityBuilderType getType() {
		return type;
	}

	public void setType(AbilityBuilderType type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
}
