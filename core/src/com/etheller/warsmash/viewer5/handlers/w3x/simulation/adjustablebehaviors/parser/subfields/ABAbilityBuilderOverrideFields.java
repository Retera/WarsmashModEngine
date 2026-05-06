package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAutocastTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABTargetTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;

public class ABAbilityBuilderOverrideFields {
	
	private ABFloatCallback areaOverride;
	private ABFloatCallback rangeOverride;
	private ABFloatCallback castTimeOverride;
	private ABFloatCallback cooldownOverride;
	private ABIntegerCallback manaCostOverride;

	private ABBooleanCallback ignoreCastTime;
	
	private ABAutocastTypeCallback autocastTypeOverride;
	
	private ABIDCallback onTooltipOverride;
	private ABIDCallback offTooltipOverride;
	
	private ABBooleanCallback physicalSpell;
	private ABBooleanCallback magicSpell;
	private ABBooleanCallback universalSpell;
	private ABBooleanCallback dispel;
	
	List<ABTargetTypeCallback> extraTargetsAllowed;
	List<ABTargetTypeCallback> excludedTargetsAllowed;

	public void updateFromParent(ABAbilityBuilderOverrideFields parent) {
		if (this.areaOverride == null)
			this.areaOverride = parent.areaOverride;
		if (this.rangeOverride == null)
			this.rangeOverride = parent.rangeOverride;
		if (this.castTimeOverride == null)
			this.castTimeOverride = parent.castTimeOverride;
		if (this.cooldownOverride == null)
			this.cooldownOverride = parent.cooldownOverride;
		if (this.manaCostOverride == null)
			this.manaCostOverride = parent.manaCostOverride;

		if (this.ignoreCastTime == null)
			this.ignoreCastTime = parent.ignoreCastTime;

		if (this.autocastTypeOverride == null)
			this.autocastTypeOverride = parent.autocastTypeOverride;

		if (this.onTooltipOverride == null)
			this.onTooltipOverride = parent.onTooltipOverride;
		if (this.offTooltipOverride == null)
			this.offTooltipOverride = parent.offTooltipOverride;

		if (this.physicalSpell == null)
			this.physicalSpell = parent.physicalSpell;
		if (this.magicSpell == null)
			this.magicSpell = parent.magicSpell;
		if (this.universalSpell == null)
			this.universalSpell = parent.universalSpell;
	}
	
	public ABFloatCallback getAreaOverride() {
		return areaOverride;
	}
	public void setAreaOverride(ABFloatCallback areaOverride) {
		this.areaOverride = areaOverride;
	}
	public ABFloatCallback getRangeOverride() {
		return rangeOverride;
	}
	public void setRangeOverride(ABFloatCallback rangeOverride) {
		this.rangeOverride = rangeOverride;
	}
	public ABFloatCallback getCastTimeOverride() {
		return castTimeOverride;
	}
	public void setCastTimeOverride(ABFloatCallback castTimeOverride) {
		this.castTimeOverride = castTimeOverride;
	}
	public ABFloatCallback getCooldownOverride() {
		return cooldownOverride;
	}
	public void setCooldownOverride(ABFloatCallback cooldownOverride) {
		this.cooldownOverride = cooldownOverride;
	}
	public ABIntegerCallback getManaCostOverride() {
		return manaCostOverride;
	}
	public void setManaCostOverride(ABIntegerCallback manaCostOverride) {
		this.manaCostOverride = manaCostOverride;
	}
	public ABBooleanCallback getIgnoreCastTime() {
		return ignoreCastTime;
	}
	public void setIgnoreCastTime(ABBooleanCallback ignoreCastTime) {
		this.ignoreCastTime = ignoreCastTime;
	}
	public ABAutocastTypeCallback getAutocastTypeOverride() {
		return autocastTypeOverride;
	}
	public void setAutocastTypeOverride(ABAutocastTypeCallback autocastTypeOverride) {
		this.autocastTypeOverride = autocastTypeOverride;
	}
	public ABIDCallback getOnTooltipOverride() {
		return onTooltipOverride;
	}
	public void setOnTooltipOverride(ABIDCallback onTooltipOverride) {
		this.onTooltipOverride = onTooltipOverride;
	}
	public ABIDCallback getOffTooltipOverride() {
		return offTooltipOverride;
	}
	public void setOffTooltipOverride(ABIDCallback offTooltipOverride) {
		this.offTooltipOverride = offTooltipOverride;
	}
	public ABBooleanCallback getPhysicalSpell() {
		return physicalSpell;
	}
	public void setPhysicalSpell(ABBooleanCallback physicalSpell) {
		this.physicalSpell = physicalSpell;
	}
	public ABBooleanCallback getMagicSpell() {
		return magicSpell;
	}
	public void setMagicSpell(ABBooleanCallback magicSpell) {
		this.magicSpell = magicSpell;
	}
	public ABBooleanCallback getUniversalSpell() {
		return universalSpell;
	}
	public void setUniversalSpell(ABBooleanCallback universalSpell) {
		this.universalSpell = universalSpell;
	}
	public ABBooleanCallback getDispel() {
		return dispel;
	}
	public void setDispel(ABBooleanCallback dispel) {
		this.dispel = dispel;
	}

	public List<ABTargetTypeCallback> getExtraTargetsAllowed() {
		return extraTargetsAllowed;
	}
	public void setExtraTargetsAllowed(List<ABTargetTypeCallback> extraTargetsAllowed) {
		this.extraTargetsAllowed = extraTargetsAllowed;
	}
	public List<ABTargetTypeCallback> getExcludedTargetsAllowed() {
		return excludedTargetsAllowed;
	}
	public void setExcludedTargetsAllowed(List<ABTargetTypeCallback> excludedTargetsAllowed) {
		this.excludedTargetsAllowed = excludedTargetsAllowed;
	}

}
