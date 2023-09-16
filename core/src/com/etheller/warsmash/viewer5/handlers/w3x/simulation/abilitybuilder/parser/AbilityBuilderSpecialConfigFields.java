package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class AbilityBuilderSpecialConfigFields {
	private Integer bufferManaRequired;
	private Integer manaDrainedPerSecond;
	
	private List<ABCondition> pointTargeted;
	private List<ABCondition> targetedSpell;

	private List<ABAction> autoAquireTarget;

	public Integer getBufferManaRequired() {
		return bufferManaRequired;
	}

	public void setBufferManaRequired(Integer bufferManaRequired) {
		this.bufferManaRequired = bufferManaRequired;
	}

	public Integer getManaDrainedPerSecond() {
		return manaDrainedPerSecond;
	}

	public void setManaDrainedPerSecond(Integer manaDrainedPerSecond) {
		this.manaDrainedPerSecond = manaDrainedPerSecond;
	}

	public List<ABCondition> getPointTargeted() {
		return pointTargeted;
	}

	public void setPointTargeted(List<ABCondition> pointTargeted) {
		this.pointTargeted = pointTargeted;
	}

	public List<ABCondition> getTargetedSpell() {
		return targetedSpell;
	}

	public void setTargetedSpell(List<ABCondition> targetedSpell) {
		this.targetedSpell = targetedSpell;
	}

	public List<ABAction> getAutoAquireTarget() {
		return autoAquireTarget;
	}

	public void setAutoAquireTarget(List<ABAction> autoAquireTarget) {
		this.autoAquireTarget = autoAquireTarget;
	}
}
