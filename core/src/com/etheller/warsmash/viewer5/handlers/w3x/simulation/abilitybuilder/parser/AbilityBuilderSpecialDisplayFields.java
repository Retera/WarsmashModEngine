package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;

public class AbilityBuilderSpecialDisplayFields {
	
	private ABBooleanCallback showOnAndOffIcons;
	
	private ABIntegerCallback foodCost;
	private ABIntegerCallback goldCost;
	private ABIntegerCallback lumberCost;
	
	private ABBooleanCallback hideAreaCursor;
	private ABBooleanCallback instantCast;
	private ABBooleanCallback castlessNoTarget;
	
	private ABBooleanCallback toggleable;
	private ABBooleanCallback castToggleOff;
	private ABBooleanCallback separateOnAndOff;
	private ABIDCallback alternateUnitId;
	
	public ABBooleanCallback getShowOnAndOffIcons() {
		return showOnAndOffIcons;
	}
	public void setShowOnAndOffIcons(ABBooleanCallback showOnAndOffIcons) {
		this.showOnAndOffIcons = showOnAndOffIcons;
	}
	public ABIntegerCallback getFoodCost() {
		return foodCost;
	}
	public void setFoodCost(ABIntegerCallback foodCost) {
		this.foodCost = foodCost;
	}
	public ABIntegerCallback getGoldCost() {
		return goldCost;
	}
	public void setGoldCost(ABIntegerCallback goldCost) {
		this.goldCost = goldCost;
	}
	public ABIntegerCallback getLumberCost() {
		return lumberCost;
	}
	public void setLumberCost(ABIntegerCallback lumberCost) {
		this.lumberCost = lumberCost;
	}
	public ABBooleanCallback getHideAreaCursor() {
		return hideAreaCursor;
	}
	public void setHideAreaCursor(ABBooleanCallback hideAreaCursor) {
		this.hideAreaCursor = hideAreaCursor;
	}
	public ABBooleanCallback getInstantCast() {
		return instantCast;
	}
	public void setInstantCast(ABBooleanCallback instantCast) {
		this.instantCast = instantCast;
	}
	public ABBooleanCallback getCastlessNoTarget() {
		return castlessNoTarget;
	}
	public void setCastlessNoTarget(ABBooleanCallback castlessNoTarget) {
		this.castlessNoTarget = castlessNoTarget;
	}
	public ABBooleanCallback getToggleable() {
		return toggleable;
	}
	public void setToggleable(ABBooleanCallback toggleable) {
		this.toggleable = toggleable;
	}
	public ABBooleanCallback getCastToggleOff() {
		return castToggleOff;
	}
	public void setCastToggleOff(ABBooleanCallback castToggleOff) {
		this.castToggleOff = castToggleOff;
	}
	public ABBooleanCallback getSeparateOnAndOff() {
		return separateOnAndOff;
	}
	public void setSeparateOnAndOff(ABBooleanCallback separateOnAndOff) {
		this.separateOnAndOff = separateOnAndOff;
	}
	public ABIDCallback getAlternateUnitId() {
		return alternateUnitId;
	}
	public void setAlternateUnitId(ABIDCallback alternateUnitId) {
		this.alternateUnitId = alternateUnitId;
	}

}
