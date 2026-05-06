package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;

public class ABAbilityBuilderSpecialDisplayFields {
	
	private ABBooleanCallback showOnAndOffIcons;
	
	private ABIntegerCallback foodCost;
	private ABIntegerCallback goldCost;
	private ABIntegerCallback lumberCost;
	
	private ABBooleanCallback hideAreaCursor;
	private ABFloatCallback areaCursorOverride;
	private ABBooleanCallback instantCast;
	private ABBooleanCallback castlessNoTarget;
	
	private ABBooleanCallback toggleable;
	private ABBooleanCallback castToggleOff;
	private ABBooleanCallback separateOnAndOff;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback isMenu;
	private ABIntegerCallback menuId;

	public void updateFromParent(ABAbilityBuilderSpecialDisplayFields parent) {
		if (this.showOnAndOffIcons == null)
			this.showOnAndOffIcons = parent.showOnAndOffIcons;

		if (this.foodCost == null)
			this.foodCost = parent.foodCost;
		if (this.goldCost == null)
			this.goldCost = parent.goldCost;
		if (this.lumberCost == null)
			this.lumberCost = parent.lumberCost;

		if (this.hideAreaCursor == null)
			this.hideAreaCursor = parent.hideAreaCursor;
		if (this.areaCursorOverride == null)
			this.areaCursorOverride = parent.areaCursorOverride;
		if (this.instantCast == null)
			this.instantCast = parent.instantCast;
		if (this.castlessNoTarget == null)
			this.castlessNoTarget = parent.castlessNoTarget;

		if (this.toggleable == null)
			this.toggleable = parent.toggleable;
		if (this.castToggleOff == null)
			this.castToggleOff = parent.castToggleOff;
		if (this.separateOnAndOff == null)
			this.separateOnAndOff = parent.separateOnAndOff;
		if (this.alternateUnitId == null)
			this.alternateUnitId = parent.alternateUnitId;
	}
	
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
	public ABFloatCallback getAreaCursorOverride() {
		return areaCursorOverride;
	}
	public void setAreaCursorOverride(ABFloatCallback areaCursorOverride) {
		this.areaCursorOverride = areaCursorOverride;
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

	public ABBooleanCallback getIsMenu() {
		return this.isMenu;
	}
	public void setIsMenu(ABBooleanCallback isMenu) {
		this.isMenu = isMenu;
	}
	public ABIntegerCallback getMenuId() {
		return this.menuId;
	}
	public void setMenuId(ABIntegerCallback menuId) {
		this.menuId = menuId;
	}

}
