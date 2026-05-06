package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.subfields;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.orderid.ABOrderIdCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeysEnum;

public class ABAbilityBuilderSpecialConfigFields {
	private ABIntegerCallback bufferManaRequired;
	private ABIntegerCallback manaDrainedPerSecond;
	
	private List<ABBooleanCallback> pointTargeted;
	private List<ABBooleanCallback> targetedSpell;

	private List<ABAction> autoAquireTarget;

	private ABIDCallback pairAbilityId;
	private ABIDCallback pairUnitId;
	private CommandStringErrorKeysEnum pairUnitTypeError;
	private CommandStringErrorKeysEnum cantTargetError;
	private CommandStringErrorKeysEnum cantPairError;
	private CommandStringErrorKeysEnum cantPairOffError;
	private ABFloatCallback pairSearchRadius;
	private ABBooleanCallback autoTargetPartner;
	private ABIntegerCallback maxPartners;
	private ABOrderIdCallback pairingOrderId;
	private ABOrderIdCallback pairingOffOrderId;
	private ABBooleanCallback orderPairedUnit;
	private ABOrderIdCallback orderPairedUnitOrderId;
	private ABOrderIdCallback orderPairedUnitOffOrderId;
	
	private CBehaviorCategory behaviorCategory;

	public void updateFromParent(ABAbilityBuilderSpecialConfigFields parent) {
		if (this.bufferManaRequired == null)
			this.bufferManaRequired = parent.bufferManaRequired;
		if (this.manaDrainedPerSecond == null)
			this.manaDrainedPerSecond = parent.manaDrainedPerSecond;

		if (this.pointTargeted == null)
			this.pointTargeted = parent.pointTargeted;
		if (this.targetedSpell == null)
			this.targetedSpell = parent.targetedSpell;

		if (this.autoAquireTarget == null)
			this.autoAquireTarget = parent.autoAquireTarget;

		if (this.pairAbilityId == null)
			this.pairAbilityId = parent.pairAbilityId;
		if (this.pairUnitId == null)
			this.pairUnitId = parent.pairUnitId;
		if (this.pairUnitTypeError == null)
			this.pairUnitTypeError = parent.pairUnitTypeError;
		if (this.cantTargetError == null)
			this.cantTargetError = parent.cantTargetError;
		if (this.cantPairError == null)
			this.cantPairError = parent.cantPairError;
		if (this.cantPairOffError == null)
			this.cantPairOffError = parent.cantPairOffError;
		if (this.pairSearchRadius == null)
			this.pairSearchRadius = parent.pairSearchRadius;
		if (this.autoTargetPartner == null)
			this.autoTargetPartner = parent.autoTargetPartner;
		if (this.maxPartners == null)
			this.maxPartners = parent.maxPartners;
		if (this.pairingOrderId == null)
			this.pairingOrderId = parent.pairingOrderId;
		if (this.pairingOffOrderId == null)
			this.pairingOffOrderId = parent.pairingOffOrderId;
		if (this.orderPairedUnit == null)
			this.orderPairedUnit = parent.orderPairedUnit;
		if (this.orderPairedUnitOrderId == null)
			this.orderPairedUnitOrderId = parent.orderPairedUnitOrderId;
		if (this.orderPairedUnitOffOrderId == null)
			this.orderPairedUnitOffOrderId = parent.orderPairedUnitOffOrderId;

		if (this.behaviorCategory == null)
			this.behaviorCategory = parent.behaviorCategory;
	}

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

	public List<ABBooleanCallback> getPointTargeted() {
		return pointTargeted;
	}

	public void setPointTargeted(List<ABBooleanCallback> pointTargeted) {
		this.pointTargeted = pointTargeted;
	}

	public List<ABBooleanCallback> getTargetedSpell() {
		return targetedSpell;
	}

	public void setTargetedSpell(List<ABBooleanCallback> targetedSpell) {
		this.targetedSpell = targetedSpell;
	}

	public List<ABAction> getAutoAquireTarget() {
		return autoAquireTarget;
	}

	public void setAutoAquireTarget(List<ABAction> autoAquireTarget) {
		this.autoAquireTarget = autoAquireTarget;
	}

	public ABIDCallback getPairAbilityId() {
		return pairAbilityId;
	}

	public void setPairAbilityId(ABIDCallback pairAbilityId) {
		this.pairAbilityId = pairAbilityId;
	}

	public ABIDCallback getPairUnitId() {
		return pairUnitId;
	}

	public void setPairUnitId(ABIDCallback pairUnitId) {
		this.pairUnitId = pairUnitId;
	}

	public CommandStringErrorKeysEnum getPairUnitTypeError() {
		return pairUnitTypeError;
	}

	public void setPairUnitTypeError(CommandStringErrorKeysEnum pairUnitTypeError) {
		this.pairUnitTypeError = pairUnitTypeError;
	}

	public CommandStringErrorKeysEnum getCantTargetError() {
		return cantTargetError;
	}

	public void setCantTargetError(CommandStringErrorKeysEnum cantTargetError) {
		this.cantTargetError = cantTargetError;
	}

	public CommandStringErrorKeysEnum getCantPairError() {
		return cantPairError;
	}

	public void setCantPairError(CommandStringErrorKeysEnum cantPairError) {
		this.cantPairError = cantPairError;
	}

	public CommandStringErrorKeysEnum getCantPairOffError() {
		return cantPairOffError;
	}

	public void setCantPairOffError(CommandStringErrorKeysEnum cantPairOffError) {
		this.cantPairOffError = cantPairOffError;
	}

	public ABFloatCallback getPairSearchRadius() {
		return pairSearchRadius;
	}

	public void setPairSearchRadius(ABFloatCallback pairSearchRadius) {
		this.pairSearchRadius = pairSearchRadius;
	}

	public ABBooleanCallback getAutoTargetPartner() {
		return autoTargetPartner;
	}

	public void setAutoTargetPartner(ABBooleanCallback autoTargetPartner) {
		this.autoTargetPartner = autoTargetPartner;
	}

	public ABIntegerCallback getMaxPartners() {
		return maxPartners;
	}

	public void setMaxPartners(ABIntegerCallback maxPartners) {
		this.maxPartners = maxPartners;
	}

	public ABOrderIdCallback getPairingOrderId() {
		return pairingOrderId;
	}

	public void setPairingOrderId(ABOrderIdCallback pairingOrderId) {
		this.pairingOrderId = pairingOrderId;
	}

	public ABOrderIdCallback getPairingOffOrderId() {
		return pairingOffOrderId;
	}

	public void setPairingOffOrderId(ABOrderIdCallback pairingOffOrderId) {
		this.pairingOffOrderId = pairingOffOrderId;
	}

	public ABBooleanCallback getOrderPairedUnit() {
		return orderPairedUnit;
	}

	public void setOrderPairedUnit(ABBooleanCallback orderPairedUnit) {
		this.orderPairedUnit = orderPairedUnit;
	}

	public ABOrderIdCallback getOrderPairedUnitOrderId() {
		return orderPairedUnitOrderId;
	}

	public void setOrderPairedUnitOrderId(ABOrderIdCallback orderPairedUnitOrderId) {
		this.orderPairedUnitOrderId = orderPairedUnitOrderId;
	}

	public ABOrderIdCallback getOrderPairedUnitOffOrderId() {
		return orderPairedUnitOffOrderId;
	}

	public void setOrderPairedUnitOffOrderId(ABOrderIdCallback orderPairedUnitOffOrderId) {
		this.orderPairedUnitOffOrderId = orderPairedUnitOffOrderId;
	}

	public CBehaviorCategory getBehaviorCategory() {
		return behaviorCategory;
	}

	public void setBehaviorCategory(CBehaviorCategory behaviorCategory) {
		this.behaviorCategory = behaviorCategory;
	}
}
