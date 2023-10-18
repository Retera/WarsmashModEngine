package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.orderid.ABOrderIdCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeysEnum;

public class AbilityBuilderSpecialConfigFields {
	private ABIntegerCallback bufferManaRequired;
	private ABIntegerCallback manaDrainedPerSecond;
	
	private List<ABCondition> pointTargeted;
	private List<ABCondition> targetedSpell;

	private List<ABAction> autoAquireTarget;

	public ABIntegerCallback getBufferManaRequired() {
		return bufferManaRequired;
	}

	public void setBufferManaRequired(ABIntegerCallback bufferManaRequired) {
		this.bufferManaRequired = bufferManaRequired;
	}

	public ABIntegerCallback getManaDrainedPerSecond() {
		return manaDrainedPerSecond;
	}

	public void setManaDrainedPerSecond(ABIntegerCallback manaDrainedPerSecond) {
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
